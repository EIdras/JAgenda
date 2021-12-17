package test;


import main.RendezvousImpl;
import main.RendezvousManagerImpl;
import myrendezvous.exceptions.RendezvousNotFound;
import org.junit.jupiter.api.*;


import java.util.Calendar;

import static org.testng.Assert.assertEquals;

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
        assertEquals(rdvManager.getTreeMap().size(), 3);
    }

    @Test
    public void updateRDV() throws RendezvousNotFound {
        rdvManager = new RendezvousManagerImpl();
        RendezvousImpl rdv = new RendezvousImpl(c, 10, "Michel");
        RendezvousImpl rdvToUpdate = (RendezvousImpl) rdvManager.addRendezvous(rdv);
        rdvToUpdate.setDescription("Jean Louis et Michel");
        RendezvousImpl rdvUpdated = (RendezvousImpl) rdvManager.updateRendezvous(rdvToUpdate);
        assertEquals(!rdvToUpdate.equals(rdvUpdated),true);
    }

    @Test
    public void removeAllRDVBefore() throws RendezvousNotFound {
        RendezvousImpl rdv = new RendezvousImpl(c, 10, "Jacques");
        rdvManager.addRendezvous(rdv);
        c.set(Calendar.MINUTE,c.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv2 = new RendezvousImpl(c, 10, "Jacques2");
        rdvManager.addRendezvous(rdv2);
        c.set(Calendar.MINUTE,c.get(Calendar.MINUTE)+1);
        Calendar cToRemoveBefore = c; // Date a laquelle on supprime tous les RDVs antérieurs
        RendezvousImpl rdv3 = new RendezvousImpl(c, 10, "Jacques3");
        rdvManager.addRendezvous(rdv3);
        c.set(Calendar.MINUTE,c.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv4 = new RendezvousImpl(c, 10, "Jacques4");
        rdvManager.addRendezvous(rdv4);
        rdvManager.removeAllRendezvousBefore(cToRemoveBefore);
        assertEquals(rdvManager.getTreeMap().size(),2);
    }


}
