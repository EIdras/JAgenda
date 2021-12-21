package test;


import main.RendezvousImpl;
import main.RendezvousManagerImpl;
import myrendezvous.Rendezvous;
import myrendezvous.exceptions.RendezvousNotFound;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.List;

public class V1_Tests {
    public RendezvousManagerImpl rdvManager;
    public Calendar c = Calendar.getInstance();

    @BeforeEach
    public void initEach(){
        rdvManager = new RendezvousManagerImpl();
    }

    @Test
    public void addRDV(){
        RendezvousImpl rdv = new RendezvousImpl(c, 10, "Michel");
        rdvManager.addRendezvous(rdv);
        c.set(Calendar.MINUTE,c.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv2 = new RendezvousImpl(c, 10, "Michel2");
        rdvManager.addRendezvous(rdv2);
        c.set(Calendar.MINUTE,c.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv3 = new RendezvousImpl(c, 10, "Michel3");
        rdvManager.addRendezvous(rdv3);
        assertEquals(3, rdvManager.getTreeMap().size());
    }

    @Test
    public void getRDVBetween() {
        RendezvousImpl rdv = new RendezvousImpl(c, 10, "Pascal");
        rdvManager.addRendezvous(rdv);

        Calendar cl2 = (Calendar) c.clone();
        cl2.set(Calendar.MINUTE,cl2.get(Calendar.MINUTE)+1);
        Calendar rdvBetweenA = (Calendar) cl2.clone();                              // Parametre 1
        RendezvousImpl rdv2 = new RendezvousImpl(cl2, 10, "Pascal2");
        rdvManager.addRendezvous(rdv2);

        Calendar cl3 = (Calendar) cl2.clone();
        cl3.set(Calendar.MINUTE,cl3.get(Calendar.MINUTE)+1);
        Calendar rdvBetweenB = (Calendar) cl3.clone();                              // Parametre 2
        RendezvousImpl rdv3 = new RendezvousImpl(cl3, 10, "Pascal3");
        rdvManager.addRendezvous(rdv3);

        Calendar cl4 = (Calendar) cl3.clone();
        cl4.set(Calendar.MINUTE,cl4.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv4 = new RendezvousImpl(cl4, 10, "Pascal4");
        rdvManager.addRendezvous(rdv4);

        List<Rendezvous> returnList = rdvManager.getRendezvousBetween(rdvBetweenA, rdvBetweenB);
        assertEquals(2, returnList.size());
    }
    @Test
    public void getRDVBefore() {
        RendezvousImpl rdv = new RendezvousImpl(c, 10, "Josette");
        rdvManager.addRendezvous(rdv);

        Calendar cl2 = (Calendar) c.clone();
        cl2.set(Calendar.MINUTE,cl2.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv2 = new RendezvousImpl(cl2, 10, "Josette2");
        rdvManager.addRendezvous(rdv2);

        Calendar cl3 = (Calendar) cl2.clone();
        cl3.set(Calendar.MINUTE,cl3.get(Calendar.MINUTE)+1);
        Calendar rdvBefore = (Calendar) cl3.clone();                                // Parametre
        RendezvousImpl rdv3 = new RendezvousImpl(cl3, 10, "Josette3");
        rdvManager.addRendezvous(rdv3);

        Calendar cl4 = (Calendar) cl3.clone();
        cl4.set(Calendar.MINUTE,cl4.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv4 = new RendezvousImpl(cl4, 10, "Josette4");
        rdvManager.addRendezvous(rdv4);

        List<Rendezvous> returnList = rdvManager.getRendezvousBefore(rdvBefore);
        assertEquals(3, returnList.size());
    }
    @Test
    public void getRDVAfter() {
        RendezvousImpl rdv = new RendezvousImpl(c, 10, "David");
        rdvManager.addRendezvous(rdv);

        Calendar cl2 = (Calendar) c.clone();
        cl2.set(Calendar.MINUTE,cl2.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv2 = new RendezvousImpl(cl2, 10, "David2");
        rdvManager.addRendezvous(rdv2);

        Calendar cl3 = (Calendar) cl2.clone();
        cl3.set(Calendar.MINUTE,cl3.get(Calendar.MINUTE)+1);
        Calendar rdvAfter = (Calendar) cl3.clone();                                 // Parametre
        RendezvousImpl rdv3 = new RendezvousImpl(cl3, 10, "David3");
        rdvManager.addRendezvous(rdv3);

        Calendar cl4 = (Calendar) cl3.clone();
        cl4.set(Calendar.MINUTE,cl4.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv4 = new RendezvousImpl(cl4, 10, "David4");
        rdvManager.addRendezvous(rdv4);

        List<Rendezvous> returnList = rdvManager.getRendezvousAfter(rdvAfter);
        assertEquals(2, returnList.size());
    }

    @Test
    public void getRDVToday(){
        RendezvousImpl rdv = new RendezvousImpl(c, 10, "René");
        rdvManager.addRendezvous(rdv);

        Calendar cl2 = (Calendar) c.clone();
        cl2.set(Calendar.MINUTE,cl2.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv2 = new RendezvousImpl(cl2, 10, "René2");
        rdvManager.addRendezvous(rdv2);

        Calendar cl3 = (Calendar) cl2.clone();
        cl3.set(Calendar.MINUTE,cl3.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv3 = new RendezvousImpl(cl3, 10, "René3");
        rdvManager.addRendezvous(rdv3);

        List<Rendezvous> returnList = rdvManager.getRendezvousToday();
        assertEquals(1, returnList.size());
        assertEquals(rdv2, returnList.get(0));
    }

    @Test
    public void updateRDV() throws RendezvousNotFound {
        rdvManager = new RendezvousManagerImpl();
        RendezvousImpl rdv = new RendezvousImpl(c, 10, "Michel");
        RendezvousImpl rdvToUpdate = (RendezvousImpl) rdvManager.addRendezvous(rdv);
        rdvToUpdate.setDescription("Jean Louis et Michel");
        RendezvousImpl rdvUpdated = (RendezvousImpl) rdvManager.updateRendezvous(rdvToUpdate);
        assertEquals(true, !rdvToUpdate.equals(rdvUpdated));
    }

    @Test
    public void removeAllRDVBefore() throws RendezvousNotFound {
        RendezvousImpl rdv = new RendezvousImpl(c, 10, "Jack");
        rdvManager.addRendezvous(rdv);

        Calendar cl2 = (Calendar) c.clone();
        cl2.set(Calendar.MINUTE,cl2.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv2 = new RendezvousImpl(cl2, 10, "Jack2");
        rdvManager.addRendezvous(rdv2);

        Calendar cl3 = (Calendar) cl2.clone();
        cl3.set(Calendar.MINUTE,cl3.get(Calendar.MINUTE)+1);
        Calendar removeBefore = (Calendar) cl3.clone();                             // Parametre
        RendezvousImpl rdv3 = new RendezvousImpl(cl3, 10, "Jack3");
        rdvManager.addRendezvous(rdv3);

        Calendar cl4 = (Calendar) cl3.clone();
        cl4.set(Calendar.MINUTE,cl4.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv4 = new RendezvousImpl(cl4, 10, "Jack4");
        rdvManager.addRendezvous(rdv4);

        assertEquals(4, rdvManager.getTreeMap().size());
        rdvManager.removeAllRendezvousBefore(removeBefore);
        assertEquals(1, rdvManager.getTreeMap().size());
    }


}
