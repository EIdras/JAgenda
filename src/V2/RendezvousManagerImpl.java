package V2;

import myrendezvous.Rendezvous;
import myrendezvous.RendezvousManager;
import myrendezvous.exceptions.RendezvousNotFound;
import myrendezvous.utils.StringComparator;

import java.util.*;
import java.util.stream.Collectors;

public class RendezvousManagerImpl implements RendezvousManager {

    TreeMap<TurboTag, RendezvousImpl> treeMap = new TreeMap<>();

    public TreeMap<TurboTag, RendezvousImpl> getTreeMap() {
        return treeMap;
    }

    @Override
    public Rendezvous addRendezvous(Rendezvous rdv) throws IllegalArgumentException{
        // Copie le rendez-vous passé en paramètre
        RendezvousImpl rdvToAdd = (RendezvousImpl) rdv;
        RendezvousImpl rdvToClone = rdvToAdd.clone();
        // Sauvegarde la copie dans sa structure interne
        treeMap.put(rdvToClone.getTag(), rdvToClone);
        // Créée une copie de la structure et y ajoute un tagging (ensemble d'attributs immuables)
        return rdvToClone.clone();
    }

    @Override
    public void removeRendezvous(Rendezvous rendezvous) throws IllegalArgumentException, RendezvousNotFound {
        RendezvousImpl rendezvous1 = (RendezvousImpl) rendezvous;
        TurboTag tag = rendezvous1.getTag();
        try {
            //System.out.println("UUID remove : "+tag.getUuid());
            treeMap.remove(tag);
        } catch (Error e){
            throw new RendezvousNotFound();
        }
    }

    @Override
    public boolean removeRendezvous(Calendar calendar) {
        if (treeMap.remove(new TurboTag(calendar)) == null){
            return false;
        }
        return true;
    }

    @Override
    public void removeAllRendezvousBefore(Calendar calendar) throws IllegalArgumentException {
        SortedMap<TurboTag, RendezvousImpl> headMap = ((TreeMap)treeMap.clone()).headMap(new TurboTag(calendar),true);
        for(TurboTag entry : headMap.keySet()) {
            treeMap.remove(entry);
        }
    }

    @Override
    public Rendezvous updateRendezvous(Rendezvous rendezvous) throws RendezvousNotFound {
        RendezvousImpl rendezvous1 = (RendezvousImpl) rendezvous;
        TurboTag tag = rendezvous1.getTag();
        RendezvousImpl rdv = treeMap.get(tag);
        if (rdv == null){
            throw new RendezvousNotFound();
        }
        else {
            rdv.setDuration(rendezvous.getDuration());
            rdv.setTime(rendezvous.getTime());
            rdv.setTitle(rendezvous.getTitle());
            rdv.setDescription(rendezvous.getDescription());

            treeMap.put(tag,rdv);
            return rdv.clone();
        }

    }

    @Override
    public List<Rendezvous> getRendezvousBetween(Calendar startTime, Calendar endTime) throws IllegalArgumentException {
        Map<TurboTag, RendezvousImpl> subMap = createSubMap(new TurboTag(startTime), new TurboTag(endTime));
        List<Rendezvous> rendezvousList = subMap.values().stream().collect(Collectors.toList());
        return rendezvousList;
    }

    @Override
    public List<Rendezvous> getRendezvousBefore(Calendar calendar) throws IllegalArgumentException {
        Map<TurboTag, RendezvousImpl> subMap = createSubMap(null, new TurboTag(calendar));
        List<Rendezvous> rendezvousList = subMap.values().stream().collect(Collectors.toList());
        return rendezvousList;
    }

    @Override
    public List<Rendezvous> getRendezvousAfter(Calendar calendar) throws IllegalArgumentException {
        Map<TurboTag, RendezvousImpl> subMap = createSubMap(new TurboTag(calendar), null);
        List<Rendezvous> rendezvousList = subMap.values().stream().collect(Collectors.toList());
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

        Map<TurboTag, RendezvousImpl> subMap = createSubMap(new TurboTag(today), new TurboTag(tomorrow));
        List<Rendezvous> rendezvousList = subMap.values().stream().collect(Collectors.toList());
        return rendezvousList;
    }

    @Override
    public boolean hasOverlap(Calendar startTime, Calendar endTime) {
        Map<TurboTag, RendezvousImpl> subMap = createSubMap(new TurboTag(startTime), new TurboTag(endTime));

        boolean hasOverlap = false;

        for(TurboTag subEntry : subMap.keySet()) { // On prend chaque Calendar de la subMap (rdv compris entre les deux paramètres)
            long thisStart      = subMap.get(subEntry).getTime().getTimeInMillis();
            long thisEnd        = thisStart + subMap.get(subEntry).getDuration() * 60000;

            SortedMap<TurboTag, RendezvousImpl> tailMap = ((NavigableMap)subMap).tailMap(subEntry, false);

            for (TurboTag tailEntry : tailMap.keySet()){   // On compare le Calendar avec tous les suivants de la tailMap (prochains rdvs)

                long compareStart   = tailMap.get(tailEntry).getTime().getTimeInMillis();

                if (thisEnd >= compareStart){
                    hasOverlap = true;
                }
            }
        }
        return hasOverlap;
    }

    @Override
    public Calendar findFreeTime(int duration, Calendar startTime, Calendar endTime) throws IllegalArgumentException {

        System.out.println("FIND FREE TIME APPELLEE");
        // On crée une headMap qui contient tous les rendez-vous débutant avant startTime
        Map<TurboTag, RendezvousImpl> headMap = createSubMap(null, new TurboTag(startTime));
        System.out.println("Nombre de RDV débutant avant startTime : " + headMap.size());
        long limiteBasse = startTime.getTimeInMillis();
        long limiteHaute = endTime.getTimeInMillis();
        Calendar freeTimeStart;
        /*
                           startTime            endTime
                ]==============|-------------------|-------------->
         */
        // On itère sur la headMap pour trouver les rendez-vous qui se terminent après startTime
        // et on ajuste la valeur de limiteBasse en fonction, pour affiner la recherche
        if (!headMap.isEmpty()) {
            Iterator<TurboTag> headMapIt = headMap.keySet().iterator();
            while (limiteBasse < endTime.getTimeInMillis() && headMapIt.hasNext()) {
                TurboTag entry = headMapIt.next();
                System.out.println("    - " + treeMap.get(entry).getTitle());
                long entryStart = headMap.get(entry).getTime().getTimeInMillis();
                long entryEnd = entryStart + headMap.get(entry).getDuration() * 60000;
                if (entryEnd > limiteBasse) {
                    limiteBasse = entryEnd;
                }
            }
            // Test si un rendez-vous débutant avant startTime se termine après endTime
            if (limiteBasse >= endTime.getTimeInMillis()) {
                System.out.println("Un rendez-vous est prévu pendant toute la période donnée.");
                return null;
            }
        }

        Calendar limiteBasseCalendar = Calendar.getInstance();
        limiteBasseCalendar.setTimeInMillis(limiteBasse);

        if (limiteBasse != startTime.getTimeInMillis()) {

            Calendar oldLimiteBasseCalendar = Calendar.getInstance();
            long oldLimiteBasse = 0;

                /*
                                   startTime            endTime
                        ]--------------|=====|-------------|-------------->
                                         limiteBasse
                */
            // Tant qu'il existe au moins un rendez-vous entre startTime et limiteBasse,
            // on vérifie si ils finissent apres startTime et si c'est le cas on actualise sa valeur
            Map<TurboTag, RendezvousImpl> betweenMap = createSubMap(new TurboTag(startTime), new TurboTag(limiteBasseCalendar));
            while (!betweenMap.isEmpty()) {
                System.out.println("Rendez-vous trouvé entre startTime et limiteBasse (" + betweenMap.size() + ")");
                oldLimiteBasse = limiteBasse;
                Iterator<TurboTag> betweenMapIt = betweenMap.keySet().iterator();
                while (betweenMapIt.hasNext()) {
                    TurboTag cal = betweenMapIt.next();
                    RendezvousImpl rdv = treeMap.get(cal);
                    long rdvEnd = (rdv.getTime().getTimeInMillis() + rdv.getDuration() * 60000);
                    if (rdvEnd > limiteBasse) {
                        System.out.println("    - Un rendez-vous se termine après limiteBasse, actualisation de la valeur");
                        limiteBasse = rdvEnd;
                    }
                }
                oldLimiteBasseCalendar.setTimeInMillis(oldLimiteBasse);
                limiteBasseCalendar.setTimeInMillis(limiteBasse);
                betweenMap = createSubMap(new TurboTag(oldLimiteBasseCalendar), new TurboTag(limiteBasseCalendar));
            }
            System.out.println("- - - - - - - - - - - - ");
        }

        /*
                           startTime            endTime
                ]--------------|-----|=============|-------------->
                                 limiteBasse
         */
        SortedMap<TurboTag, RendezvousImpl> subMap = (SortedMap) createSubMap(new TurboTag(limiteBasseCalendar), new TurboTag(endTime));
        Calendar limiteHauteCalendar = null;
        if (!subMap.isEmpty()) {
        /*
                       startTime                         endTime
                ]----------|---------|=============|--------|----->
                                 limiteBasse  limiteHaute
         */
            RendezvousImpl firstRDV = treeMap.get(subMap.firstKey());
            limiteHaute = firstRDV.getTime().getTimeInMillis();
            limiteHauteCalendar = firstRDV.getTime();
        }

        // Si la durée du premier créneau dispo. est supérieure ou égale à la durée recherchée,
        // la valeur à retourner est la limite basse soit l'instant de départ de ce créneau

        if ((limiteBasse + (duration * 60000)) <= limiteHaute) {
            System.out.println(" ~~~ FREE TIME FOUND ~~~ ");
            freeTimeStart = limiteBasseCalendar;
        }
        // Si la durée libre n'est pas suffisante, on cherche un autre créneau dispo après, en appellant récursivement la méthode
        else {
            System.out.println("    ---> Créneau libre trouvé mais durée insuffisante, recherche du prochain créneau");
            freeTimeStart = findFreeTime(duration, limiteHauteCalendar, endTime);
        }
        return freeTimeStart;
    }


    @Override
    public List<Rendezvous> findRendezvousByTitleEqual(String search, Calendar startTime, Calendar endTime) {
        List<Rendezvous> returnList = new ArrayList<>();
        Map<TurboTag, RendezvousImpl> subMap = createSubMap(new TurboTag(startTime), new TurboTag(endTime));
        for(TurboTag entry : subMap.keySet()) {
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
        Map<TurboTag, RendezvousImpl> subMap = createSubMap(new TurboTag(startTime), new TurboTag(endTime));
        for(TurboTag entry : subMap.keySet()) {
            String rdvName = subMap.get(entry).getTitle();
            if (StringComparator.isAlike(rdvName, search)){
                returnList.add(subMap.get(entry));
            }
        }
        return returnList;
    }

    private Map<TurboTag, RendezvousImpl> createSubMap(TurboTag startTime, TurboTag endTime) {
        Map<TurboTag, RendezvousImpl> subMap;
        if (!(startTime == null)){
            if (startTime.getCalendar() == null && startTime.getUuid() == null) startTime = null;
        }
        if(!(endTime == null)){
            if (endTime.getCalendar() == null && endTime.getUuid() == null) endTime = null;
        }

        if      (startTime == null && endTime == null)      subMap = (Map<TurboTag, RendezvousImpl>) treeMap.clone();
        else if (startTime == null)                                       subMap = ((TreeMap)treeMap.clone()).headMap(endTime, true);
        else if (endTime == null)                                         subMap = ((TreeMap)treeMap.clone()).tailMap(startTime, true);
        else if (startTime.getCalendar().compareTo(endTime.getCalendar())>0)            throw new IllegalArgumentException("startTime > endTime");
        else                                                                            subMap = ((TreeMap)treeMap.clone()).subMap(startTime, true, endTime, true);
        return subMap;
    }
}
