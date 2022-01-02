import main.RendezvousImpl;
import main.RendezvousManagerImpl;
import myrendezvous.Rendezvous;
import myrendezvous.exceptions.RendezvousNotFound;
import org.junit.jupiter.api.*;

import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    public void removeRDV() throws RendezvousNotFound {
        RendezvousImpl rdv = new RendezvousImpl(c, 10, "Francis");
        rdvManager.addRendezvous(rdv);
        c.set(Calendar.MINUTE,c.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv2 = new RendezvousImpl(c, 10, "Francis2");
        rdvManager.addRendezvous(rdv2);
        c.set(Calendar.MINUTE,c.get(Calendar.MINUTE)+1);
        RendezvousImpl rdv3 = new RendezvousImpl(c, 10, "Francis3");
        rdvManager.addRendezvous(rdv3);
        assertEquals(3, rdvManager.getTreeMap().size());
        rdvManager.removeRendezvous(rdv2);
        assertEquals(2, rdvManager.getTreeMap().size());
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
    public void hasOverlapNow() {
        RendezvousImpl rdv = new RendezvousImpl(c, 25, "Fabrice");
        rdvManager.addRendezvous(rdv);

        Calendar clTest1 = (Calendar) c.clone();                        // Test1
        clTest1.set(Calendar.MINUTE,clTest1.get(Calendar.MINUTE)+27);   // Test1

        Calendar clTest2 = (Calendar) c.clone();                        // Test2
        clTest2.set(Calendar.MINUTE,clTest2.get(Calendar.MINUTE)+40);   // Test2

        Calendar cl2 = (Calendar) c.clone();
        cl2.set(Calendar.MINUTE,cl2.get(Calendar.MINUTE)+10);
        RendezvousImpl rdv2 = new RendezvousImpl(cl2, 12, "Fabrice2");
        rdvManager.addRendezvous(rdv2);

        Calendar cl3 = (Calendar) cl2.clone();
        cl3.set(Calendar.MINUTE,cl3.get(Calendar.MINUTE)+10);

        RendezvousImpl rdv3 = new RendezvousImpl(cl3, 8, "Fabrice3");
        rdvManager.addRendezvous(rdv3);

        Calendar cl4 = (Calendar) cl3.clone();
        cl4.set(Calendar.MINUTE,cl4.get(Calendar.MINUTE)+10);
        RendezvousImpl rdv4 = new RendezvousImpl(cl4, 5, "Fabrice4");
        rdvManager.addRendezvous(rdv4);

        assertEquals(true, rdvManager.hasOverlap(c,cl2));
        assertEquals(true, rdvManager.hasOverlap(cl2,cl3));
        assertEquals(true, rdvManager.hasOverlap(cl2,clTest1));
        assertEquals(true, rdvManager.hasOverlap(cl2,clTest2));
        assertEquals(false, rdvManager.hasOverlap(clTest1,clTest2));
        assertEquals(false, rdvManager.hasOverlap(null,c));
        assertEquals(false, rdvManager.hasOverlap(cl3,null));
        assertEquals(true, rdvManager.hasOverlap(cl2,null));
        assertEquals(true, rdvManager.hasOverlap(null,cl3));
    }

    @Test
    public void testHasOverlapTrue() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.DECEMBER, 5, 18, 50);
        RendezvousImpl rdv = new RendezvousImpl(calendar, 50, "Meeting");

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2021, Calendar.DECEMBER, 5, 18, 59);
        RendezvousImpl rdv1 = new RendezvousImpl(calendar1, 20, "Meeting");

        Calendar startTime = Calendar.getInstance();
        startTime.set(2021, Calendar.DECEMBER, 5, 16, 50);
        Calendar endTime = Calendar.getInstance();
        endTime.set(2022, Calendar.JANUARY, 15, 18, 29);

        rdvManager.addRendezvous(rdv);
        rdvManager.addRendezvous(rdv1);

        assertEquals(true, rdvManager.hasOverlap(startTime, endTime));
    }

    @Test
    public void testHasOverlapFalse() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.DECEMBER, 5, 18, 50);
        RendezvousImpl rdv = new RendezvousImpl(calendar, 50, "Meeting");

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2021, Calendar.DECEMBER, 6, 18, 59);
        RendezvousImpl rdv1 = new RendezvousImpl(calendar1, 20, "Meeting");

        Calendar startTime = Calendar.getInstance();
        startTime.set(2021, Calendar.DECEMBER, 5, 16, 50);
        Calendar endTime = Calendar.getInstance();
        endTime.set(2022, Calendar.DECEMBER, 18, 18, 29);

        rdvManager.addRendezvous(rdv);
        rdvManager.addRendezvous(rdv1);

        assertEquals(false, rdvManager.hasOverlap(startTime, endTime));
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
        assertEquals(3, returnList.size());
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

    @Test
    public void findRDVTitleEqual() throws RendezvousNotFound {
        String title = "Lorem ipsum Dolor sit amet";
        RendezvousImpl rdv = new RendezvousImpl(c, 30, title);
        rdvManager.addRendezvous(rdv);

        assertEquals(rdv,rdvManager.findRendezvousByTitleEqual(title, null, null).get(0));                                // Même titre
        assertEquals(rdv,rdvManager.findRendezvousByTitleEqual("lorem ipsum dolor sit amet", null, null).get(0));   // Sensibilité à la casse
        assertTrue(rdvManager.findRendezvousByTitleEqual("LoremipsumDolorsitamet", null, null).isEmpty());          // Sans espaces
    }

    @Test
    public void findRDVTitleALike() throws RendezvousNotFound {
        String title = "Lorem ipsum Dolor sit amet";
        RendezvousImpl rdv = new RendezvousImpl(c, 30, title);
        rdvManager.addRendezvous(rdv);

        assertEquals(rdv,rdvManager.findRendezvousByTitleALike(title, null, null).get(0));                                // Même titre
        assertEquals(rdv,rdvManager.findRendezvousByTitleALike("lorem ipsum dolor sit amet", null, null).get(0));   // Sensibilité à la casse
        assertFalse(rdvManager.findRendezvousByTitleALike("LoremipsumDolorsitamet", null, null).isEmpty());         // Sans espaces (ressemblant)
        assertFalse(rdvManager.findRendezvousByTitleALike("lo em ipsdm daaor szt amet", null, null).isEmpty());     // Est ressemblant
        assertTrue(rdvManager.findRendezvousByTitleALike("lnnen ipasm daaot sst zeet", null, null).isEmpty());      // N'est pas assez ressemblant
    }

    @Test
    public void findFreeTime(){

    }

}
