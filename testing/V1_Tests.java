import V1.RendezvousImpl;
import V1.RendezvousManagerImpl;
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

        c.set(Calendar.MINUTE,c.get(Calendar.MINUTE)+1);
        RendezvousImpl rdvNeg = new RendezvousImpl(c, -10, "Négatif");
        rdvManager.addRendezvous(rdvNeg);
        assertEquals(4, rdvManager.getTreeMap().size());

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
        Calendar cl5 = (Calendar) cl4.clone();
        cl5.set(Calendar.MINUTE, cl5.get(Calendar.MINUTE)+1);

        List<Rendezvous> returnList = rdvManager.getRendezvousBetween(rdvBetweenA, rdvBetweenB);
        assertEquals(2, returnList.size());
        List<Rendezvous> nullList = rdvManager.getRendezvousBetween(cl5, cl5);
        assertEquals(0, nullList.size());
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

        assertTrue(rdvManager.hasOverlap(c, cl2));
        assertTrue(rdvManager.hasOverlap(cl2, cl3));
        assertTrue(rdvManager.hasOverlap(cl2, clTest1));
        assertTrue(rdvManager.hasOverlap(cl2, clTest2));
        assertFalse(rdvManager.hasOverlap(clTest1, clTest2));
        assertFalse(rdvManager.hasOverlap(null, c));
        assertFalse(rdvManager.hasOverlap(cl3, null));
        assertTrue(rdvManager.hasOverlap(cl2, null));
        assertTrue(rdvManager.hasOverlap(null, cl3));
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
    public void updateRDV() {
        rdvManager = new RendezvousManagerImpl();
        RendezvousImpl rdv = new RendezvousImpl(c, 10, "Michel");
        RendezvousImpl rdvToUpdate = (RendezvousImpl) rdvManager.addRendezvous(rdv);
        rdvToUpdate.setDescription("Jean Louis et Michel");
        RendezvousImpl rdvUpdated = (RendezvousImpl) rdvManager.updateRendezvous(rdvToUpdate);
        assertEquals(rdvToUpdate, rdvUpdated);
    }

    @Test
    public void removeAllRDVBefore() {
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
    public void findRDVTitleEqual() {
        String title = "Lorem ipsum Dolor sit amet";
        RendezvousImpl rdv = new RendezvousImpl(c, 30, title);
        rdvManager.addRendezvous(rdv);

        assertEquals(rdv,rdvManager.findRendezvousByTitleEqual(title, null, null).get(0));                                // Même titre
        assertEquals(rdv,rdvManager.findRendezvousByTitleEqual("lorem ipsum dolor sit amet", null, null).get(0));   // Sensibilité à la casse
        assertTrue(rdvManager.findRendezvousByTitleEqual("LoremipsumDolorsitamet", null, null).isEmpty());          // Sans espaces
    }

    @Test
    public void findRDVTitleALike() {
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

        /*
                                        [expectedShort]  [expectedLong]
                                                 :         :
                             startTime           :         :               endTime
                                 |     rdv3      V         :                  |
            rdv1   rdv2          |      |=======>     rdv4 V          rdv5    |
        ]----|=>----|============|==========>---------|===>------------|======|==>--------------->
         */

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2021, Calendar.DECEMBER, 5, 16, 0);
        RendezvousImpl rdv1 = new RendezvousImpl(calendar1, 15, "Goûter", "Vive le goûter");

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(2021, Calendar.DECEMBER, 7, 12, 30);
        RendezvousImpl rdv2 = new RendezvousImpl(calendar2, 2880 /* dure 2 jours, finit le 9 a 12h30 */, "Week-end");

        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(2021, Calendar.DECEMBER, 8, 19, 15);
        RendezvousImpl rdv3 = new RendezvousImpl(calendar3, 1125 /* finit le 9 à 14h */, "Soirée");

        /* Il y a 23h de temps libre entre rdv3 et rdv4 soit 1380 minutes */

        Calendar calendar4 = Calendar.getInstance();
        calendar4.set(2021, Calendar.DECEMBER, 10, 12, 0);
        RendezvousImpl rdv4 = new RendezvousImpl(calendar4, 60 /* finit le 10 à 13h */, "Restaurant");

        /* Il y a 40h de temps libre entre rdv4 et rdv5 soit 2400 minutes */

        Calendar calendar5 = Calendar.getInstance();
        calendar5.set(2021, Calendar.DECEMBER, 12, 9, 0);
        RendezvousImpl rdv5 = new RendezvousImpl(calendar5, 240 /* finit le 12 à 13h */, "Réunion");

        Calendar startTime = Calendar.getInstance();                                    // startTime
        startTime.set(2021, Calendar.DECEMBER, 8, 12, 30);    // 8 décembre 12h30

        Calendar endTime = Calendar.getInstance();                                      // endTime
        endTime.set(2021, Calendar.DECEMBER, 12, 12, 0);      // 12 décembre 12h00


        Calendar expectedLongDuration = Calendar.getInstance();                                      // expectedLongDuration
        expectedLongDuration.set(Calendar.SECOND, 0);
        expectedLongDuration.set(Calendar.MILLISECOND, 0);
        expectedLongDuration.set(2021, Calendar.DECEMBER, 10, 13, 0);      // fin de rdv4 soit le 10 décembre 13h00

        Calendar expectedShortDuration = Calendar.getInstance();                                     // expectedShortDuration
        expectedShortDuration.set(Calendar.SECOND, 0);
        expectedShortDuration.set(Calendar.MILLISECOND, 0);
        expectedShortDuration.set(2021, Calendar.DECEMBER, 9, 14, 0);      // fin de rdv3 soit le 9 décembre 14h00

        rdvManager.addRendezvous(rdv1);
        rdvManager.addRendezvous(rdv2);
        rdvManager.addRendezvous(rdv3);
        rdvManager.addRendezvous(rdv4);
        rdvManager.addRendezvous(rdv5);

        System.out.println(rdv1);

        assertEquals(expectedLongDuration,  rdvManager.findFreeTime(1440, startTime, endTime));
        assertEquals(expectedShortDuration, rdvManager.findFreeTime(120, startTime, endTime));
        assertNull(rdvManager.findFreeTime(120, rdv2.getTime(), rdv3.getTime()));      // Test si rdv durant toute la période
        assertNull(rdvManager.findFreeTime(5000, rdv2.getTime(), rdv3.getTime()));     // Test si duration trop élevé pour la période
    }

}
