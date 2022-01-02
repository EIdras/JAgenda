package main;
// TODO : vérifier si ca fait pas de la merde de rentrer une durée négative de rendez-vous
import myrendezvous.Rendezvous;
import myrendezvous.RendezvousManager;
import myrendezvous.exceptions.RendezvousNotFound;
import myrendezvous.utils.StringComparator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class RendezvousManagerImpl implements RendezvousManager {

    TreeMap<Calendar, RendezvousImpl> treeMap = new TreeMap<>();

    public TreeMap<Calendar, RendezvousImpl> getTreeMap() {
        return treeMap;
    }

    @Override
    public Rendezvous addRendezvous(Rendezvous rdv) throws IllegalArgumentException {
            // Copie le rendez-vous passé en paramètre
            RendezvousImpl rdvToAdd = new RendezvousImpl(rdv.getTime(), rdv.getDuration(), rdv.getTitle(), rdv.getDescription());
            RendezvousImpl rdvToClone = rdvToAdd.clone();
            // Sauvegarde la copie dans sa structure interne
            treeMap.put(rdvToClone.getTag(), rdvToClone);
            // Créée une copie de la structure et y ajoute un tagging (ensemble d'attributs immuables)
            return rdvToClone.clone();
    }

    @Override
    public void removeRendezvous(Rendezvous rendezvous) throws IllegalArgumentException, RendezvousNotFound {
        RendezvousImpl rendezvous1 = new RendezvousImpl(rendezvous.getTime(), rendezvous.getDuration(), rendezvous.getTitle(), rendezvous.getDescription());
        Calendar tag = rendezvous1.getTag();
        RendezvousImpl rdv = treeMap.get(tag);
        try {
            treeMap.remove(tag);
        } catch (Error e){
            throw new RendezvousNotFound();
        }

    }

    @Override
    public boolean removeRendezvous(Calendar calendar) {
        return false;
    }

    @Override
    public void removeAllRendezvousBefore(Calendar calendar) throws IllegalArgumentException {
        // Création d'une headMap qui correspond à une vue d'une portion de la treeMap
        // dont les valeurs de clés sont inférieures ou égales au paramètre calendar
        SortedMap<Calendar, RendezvousImpl> headMap = ((TreeMap)treeMap.clone()).headMap(calendar,true);
        for(Calendar entry : headMap.keySet()) {
            treeMap.remove(entry);
        }
    }

    @Override
    public Rendezvous updateRendezvous(Rendezvous rendezvous) throws RendezvousNotFound {
        RendezvousImpl rendezvous1 = new RendezvousImpl(rendezvous.getTime(), rendezvous.getDuration(), rendezvous.getTitle(), rendezvous.getDescription());
        Calendar tag = rendezvous1.getTag();
        RendezvousImpl rdv = treeMap.get(tag);

        rdv.setDuration(rendezvous.getDuration());
        rdv.setTime(rendezvous.getTime());
        rdv.setTitle(rendezvous.getTitle());
        rdv.setDescription(rendezvous.getDescription());

        treeMap.put(tag,rdv);
        return rdv.clone();
    }

    @Override
    public List<Rendezvous> getRendezvousBetween(Calendar startTime, Calendar endTime) throws IllegalArgumentException {
        List<Rendezvous> rendezvousList = new ArrayList<>();
        for(Map.Entry<Calendar, RendezvousImpl> entry : treeMap.entrySet()) {
            // Si la valeur de l'instant de départ de l'entrée se situe entre
            // la valeur de l'instant de départ de chacun des deux paramètres
            if ((entry.getValue().getTime().getTime().compareTo(startTime.getTime())) >= 0
                    && (entry.getValue().getTime().getTime().compareTo(endTime.getTime())) <= 0){
                // On ajoute une copie de l'entrée à la liste
                rendezvousList.add(((treeMap.get(entry.getKey()))).clone());
            }
        }
        return rendezvousList;
    }

    @Override
    public List<Rendezvous> getRendezvousBefore(Calendar calendar) throws IllegalArgumentException {
        List<Rendezvous> rendezvousList = new ArrayList<>();
        for(Map.Entry<Calendar, RendezvousImpl> entry : treeMap.entrySet()) {
            // Si la valeur de l'instant de départ de l'argument se situe avant
            // la valeur de l'instant de départ de l'entrée
            if ((entry.getValue().getTime().getTime().compareTo(calendar.getTime())) <= 0){
                // On ajoute une copie de l'entrée à la liste
                rendezvousList.add(((treeMap.get(entry.getKey()))).clone());
            }
        }
        return rendezvousList;
    }

    @Override
    public List<Rendezvous> getRendezvousAfter(Calendar calendar) throws IllegalArgumentException {
        List<Rendezvous> rendezvousList = new ArrayList<>();
        for(Map.Entry<Calendar, RendezvousImpl> entry : treeMap.entrySet()) {
            // Si la valeur de l'instant de départ de l'argument se situe après
            // la valeur de l'instant de départ de l'entrée
            if ((entry.getValue().getTime().getTime().compareTo(calendar.getTime())) >= 0){
                // On ajoute une copie de l'entrée à la liste
                rendezvousList.add(((treeMap.get(entry.getKey()))).clone());
            }
        }
        return rendezvousList;
    }

    @Override
    public List<Rendezvous> getRendezvousToday() {

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        /*
        // DEBUG
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        int i = 0;
        for(Calendar entry : treeMap.keySet()) {
            System.out.println(i + " : " + dateFormat.format(entry.getTime()) );
            i++;
        }
        System.out.println("Today : " + dateFormat.format(today.getTime()) );
        System.out.println("Tomorrow : " + dateFormat.format(tomorrow.getTime()) );
        */

        SortedMap<Calendar, RendezvousImpl> subMap =
                ((TreeMap)treeMap.clone()).subMap(today, true, tomorrow, false);
        List<Rendezvous> rendezvousList = new ArrayList<>();
        for(Calendar entry : subMap.keySet()) {
            rendezvousList.add(subMap.get(entry));
        }
        return rendezvousList;
    }

    @Override
    public boolean hasOverlap(Calendar startTime, Calendar endTime) {
        Map<Calendar, RendezvousImpl> subMap = createSubMap(startTime, endTime);

        //DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");    // DEBUG
        //System.out.println("START   : " + ((startTime==null) ? "null" : dateFormat.format(startTime.getTime())));      // DEBUG
        //System.out.println("END     : " + ((endTime==null) ? "null" : dateFormat.format(endTime.getTime())));        // DEBUG

        boolean hasOverlap = false;
        //System.out.println("Nombre de rdv trouvés : " + subMap.size());                 // DEBUG
        //int i = 1;                                                                      // DEBUG
        //for(Calendar subEntry : subMap.keySet()) {                                      // DEBUG
        //    System.out.println("    - Entrée n°"+i+" : " + subMap.get(subEntry).getTitle()+ " - " + dateFormat.format(subMap.get(subEntry).getTime().getTime()));
        //    i++;                                                                        // DEBUG
        //}                                                                               // DEBUG
        for(Calendar subEntry : subMap.keySet()) { // On prend chaque Calendar de la subMap (rdv compris entre les deux paramètres)
            long thisStart      = subMap.get(subEntry).getTime().getTimeInMillis();
            long thisEnd        = thisStart + subMap.get(subEntry).getDuration() * 60000;

            SortedMap<Calendar, RendezvousImpl> tailMap = ((NavigableMap)subMap).tailMap(subEntry, false);
            //System.out.println("    - Rendez-vous postérieurs au RDV actuel : " + tailMap.size());  // DEBUG
            for (Calendar tailEntry : tailMap.keySet()){   // On compare le Calendar avec tous les suivants de la tailMap (prochains rdvs)

                long compareStart   = tailMap.get(tailEntry).getTime().getTimeInMillis();
                long compareEnd     = compareStart + tailMap.get(tailEntry).getDuration() * 60000;
                //System.out.println("thisStart = "+thisStart+", thisEnd = "+thisEnd+", compareStart = "+compareStart+", compareEnd = "+compareEnd);  // DEBUG


                if (thisEnd >= compareStart){
                    hasOverlap = true;
                }

            }

        }
        return hasOverlap;
    }

    @Override
    public Calendar findFreeTime(int duration, Calendar startTime, Calendar endTime) throws IllegalArgumentException {
        Map<Calendar, RendezvousImpl> headMap = createSubMap(null, startTime);
        long limiteBasse = startTime.getTimeInMillis();

        Iterator<Calendar> headMapIt = headMap.keySet().iterator();

        while (limiteBasse < endTime.getTimeInMillis() && headMapIt.hasNext()){
            Calendar entry = headMapIt.next();
            long entryStart = headMap.get(entry).getTime().getTimeInMillis();
            long entryEnd = entryStart + headMap.get(entry).getDuration() * 60000;
            if (entryEnd > limiteBasse){
                limiteBasse = entryEnd;
            }
        }
        if (limiteBasse < endTime.getTimeInMillis()){
            System.out.println("Un rendez-vous est prévu pendant toute la période donnée.");
        }

        /*
        Date date = new Date(limiteBasse); Calendar limiteBasseCalendar = Calendar.getInstance(); limiteBasseCalendar.setTimeInMillis(limiteBasse);

        Map<Calendar, RendezvousImpl> subMap = createSubMap(limiteBasseCalendar, endTime);
        Iterator<Calendar> subMapIt = subMap.keySet().iterator();
        while (subMapIt.hasNext()){
            Calendar entry = subMapIt.next();
            if (entry.getTime())
        }
        */

        return null;
    }

    @Override
    public List<Rendezvous> findRendezvousByTitleEqual(String search, Calendar startTime, Calendar endTime) {
        List<Rendezvous> returnList = new ArrayList<>();
        Map<Calendar, RendezvousImpl> subMap = createSubMap(startTime, endTime);
        for(Calendar entry : subMap.keySet()) {
            String rdvName = subMap.get(entry).getTitle();
            if (StringComparator.isEqualNoCase(rdvName, search)){
                returnList.add(subMap.get(entry));
            }
        }
        return returnList;
    }

    @Override
    public List<Rendezvous> findRendezvousByTitleALike(String search, Calendar startTime, Calendar endTime) {
        List<Rendezvous> returnList = new ArrayList<>();
        Map<Calendar, RendezvousImpl> subMap = createSubMap(startTime, endTime);
        for(Calendar entry : subMap.keySet()) {
            String rdvName = subMap.get(entry).getTitle();
            if (StringComparator.isAlike(rdvName, search)){
                returnList.add(subMap.get(entry));
            }
        }
        return returnList;
    }

    private Map<Calendar, RendezvousImpl> createSubMap(Calendar startTime, Calendar endTime) {
        Map<Calendar, RendezvousImpl> subMap;
        if      (startTime == null && endTime == null)  subMap = (Map<Calendar, RendezvousImpl>) treeMap.clone();
        else if (startTime == null)                     subMap = ((TreeMap)treeMap.clone()).headMap(endTime, true);
        else if (endTime == null)                       subMap = ((TreeMap)treeMap.clone()).tailMap(startTime, true);
        else                                            subMap = ((TreeMap)treeMap.clone()).subMap(startTime, true, endTime, true);
        return subMap;
    }
}
