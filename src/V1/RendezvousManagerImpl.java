package V1;
// TODO : vérifier si ca fait pas de la merde de rentrer une durée négative de rendez-vous

import myrendezvous.Rendezvous;
import myrendezvous.RendezvousManager;
import myrendezvous.exceptions.RendezvousNotFound;
import myrendezvous.utils.StringComparator;

import java.util.*;
import java.util.stream.Collectors;

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
            // Sauvegarde la copie dans sa structure interne + tagging
            treeMap.put(rdvToClone.getTag(), rdvToClone);
            return rdvToClone.clone();
    }

    @Override
    public void removeRendezvous(Rendezvous rdvToRemove) throws IllegalArgumentException, RendezvousNotFound {
        RendezvousImpl rdv = new RendezvousImpl(rdvToRemove.getTime(), rdvToRemove.getDuration(), rdvToRemove.getTitle(), rdvToRemove.getDescription());
        Calendar tag = rdv.getTag();
        try {
            treeMap.remove(tag);
        } catch (Error e){
            throw new RendezvousNotFound();
        }

    }

    @Override
    public boolean removeRendezvous(Calendar calendar) {
        return treeMap.remove(calendar) != null;
    }

    @Override
    public void removeAllRendezvousBefore(Calendar calendar) throws IllegalArgumentException {
        // Création d'une headMap qui correspond à une vue d'une portion de la treeMap pour les rendez-vous débutant avant le paramètre calendar
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
        Map<Calendar, RendezvousImpl> subMap = createSubMap(startTime, endTime);
        List<Rendezvous> rendezvousList = subMap.values().stream().collect(Collectors.toList());
        return rendezvousList;
    }

    @Override
    public List<Rendezvous> getRendezvousBefore(Calendar calendar) throws IllegalArgumentException {
        Map<Calendar, RendezvousImpl> headMap = createSubMap(null, calendar);
        List<Rendezvous> rendezvousList = headMap.values().stream().collect(Collectors.toList());
        return rendezvousList;
    }

    @Override
    public List<Rendezvous> getRendezvousAfter(Calendar calendar) throws IllegalArgumentException {
        Map<Calendar, RendezvousImpl> headMap = createSubMap(calendar, null);
        List<Rendezvous> rendezvousList = headMap.values().stream().collect(Collectors.toList());
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

        Map<Calendar, RendezvousImpl> subMap = createSubMap(today, tomorrow);
        List<Rendezvous> rendezvousList = subMap.values().stream().collect(Collectors.toList());
        return rendezvousList;
    }

    // TODO optimiser en ne comparant que avec le rdv suivant (Pareil V2)
    @Override
    public boolean hasOverlap(Calendar startTime, Calendar endTime) {
        Map<Calendar, RendezvousImpl> subMap = createSubMap(startTime, endTime);

        boolean hasOverlap = false;

        for(Calendar subEntry : subMap.keySet()) { // On prend chaque Calendar de la subMap (rdv compris entre les deux paramètres)
            long thisStart      = subMap.get(subEntry).getTime().getTimeInMillis();
            long thisEnd        = thisStart + subMap.get(subEntry).getDuration() * 60000;

            SortedMap<Calendar, RendezvousImpl> tailMap = ((NavigableMap)subMap).tailMap(subEntry, false);

            for (Calendar tailEntry : tailMap.keySet()){   // On compare le Calendar avec tous les suivants de la tailMap (prochains rdvs)

                long compareStart   = tailMap.get(tailEntry).getTime().getTimeInMillis();

                if (thisEnd >= compareStart){
                    hasOverlap = true;
                }

            }

        }
        return hasOverlap;
    }

    // TODO : Virer commentaires, gérer avec les secondes  / millisecondes à zéro sinon problemes
    @Override
    public Calendar findFreeTime(int duration, Calendar startTime, Calendar endTime) throws IllegalArgumentException {

        // On crée une headMap qui contient tous les rendez-vous débutant avant startTime
        Map<Calendar, RendezvousImpl> headMap = createSubMap(null, startTime);
        // Définition de variables représentant les limites de recherche
        long limiteBasse = startTime.getTimeInMillis();
        long limiteHaute = endTime.getTimeInMillis();
        Calendar freeTimeStart;     // Résultat à retourner
        /*
                           startTime            endTime
                ]==============|-------------------|-------------->
         */
        // On itère sur la headMap pour trouver les rendez-vous qui se terminent après startTime
        // et on ajuste la valeur de limiteBasse en fonction, pour affiner la recherche
            if (!headMap.isEmpty()){
                Iterator<Calendar> headMapIt = headMap.keySet().iterator();
                while (limiteBasse < endTime.getTimeInMillis() && headMapIt.hasNext()){
                    Calendar entry = headMapIt.next();
                    long entryStart = headMap.get(entry).getTime().getTimeInMillis();
                    long entryEnd = entryStart + headMap.get(entry).getDuration() * 60000;
                    if (entryEnd > limiteBasse){
                        limiteBasse = entryEnd;
                    }
                }
                // Test si un rendez-vous débutant avant startTime se termine après endTime
                // Si c'est le cas, il occupe tout le temps, on renvoie donc null
                if (limiteBasse >= endTime.getTimeInMillis()){
                    return null;
                }
            }

            Calendar limiteBasseCalendar = Calendar.getInstance();
            limiteBasseCalendar.setTimeInMillis(limiteBasse);

            if (limiteBasse != startTime.getTimeInMillis()){

                Calendar oldLimiteBasseCalendar = Calendar.getInstance();
                long oldLimiteBasse = 0;

                /*
                                   startTime            endTime
                        ]--------------|=====|-------------|-------------->
                                         limiteBasse
                */
                // Tant qu'il existe au moins un rendez-vous entre startTime et limiteBasse,
                // on vérifie si ils finissent apres startTime et si c'est le cas on actualise sa valeur
                Map<Calendar, RendezvousImpl> betweenMap = createSubMap(startTime, limiteBasseCalendar);
                while (!betweenMap.isEmpty()){
                    System.out.println("Rendez-vous trouvé entre startTime et limiteBasse ("+betweenMap.size()+")");
                    oldLimiteBasse = limiteBasse;
                    Iterator<Calendar> betweenMapIt = betweenMap.keySet().iterator();
                    while (betweenMapIt.hasNext()){
                        Calendar cal = betweenMapIt.next();
                        RendezvousImpl rdv = treeMap.get(cal);
                        long rdvEnd = (rdv.getTime().getTimeInMillis() + rdv.getDuration() * 60000);
                        if (rdvEnd > limiteBasse){
                            System.out.println("    - Un rendez-vous se termine après limiteBasse, actualisation de la valeur");
                            limiteBasse = rdvEnd;
                        }
                    }
                    oldLimiteBasseCalendar.setTimeInMillis(oldLimiteBasse);
                    limiteBasseCalendar.setTimeInMillis(limiteBasse);
                    betweenMap = createSubMap(oldLimiteBasseCalendar, limiteBasseCalendar);
                }

            }
        /*
                           startTime                     endTime
                ]--------------|-----|======================|----->
                                 limiteBasse
         */
        SortedMap<Calendar, RendezvousImpl> subMap = (SortedMap) createSubMap(limiteBasseCalendar, endTime);
        Calendar limiteHauteCalendar = null;
        if (!subMap.isEmpty()){
        /*
                       startTime                         endTime
                ]----------|---------|=============|--------|----->
                                 limiteBasse  limiteHaute
         */
            RendezvousImpl firstRDV = treeMap.get(subMap.firstKey());
            limiteHaute = firstRDV.getTime().getTimeInMillis();
            limiteHauteCalendar = firstRDV.getTime();
        }

        // Si la durée passée en paramètre est dispo entre la limite basse et haute, on renvoie l'instant de départ
        if ((limiteBasse + (duration * 60000)) <= limiteHaute){
            freeTimeStart = limiteBasseCalendar;
        }
        // Si la durée libre n'est pas suffisante, on cherche un autre créneau dispo après, en appellant récursivement la méthode
        else {
            freeTimeStart = findFreeTime(duration, limiteHauteCalendar, endTime);
        }
        return freeTimeStart;
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
        else if (startTime.compareTo(endTime)>0)        throw new IllegalArgumentException("startTime > endTime");
        else                                            subMap = ((TreeMap)treeMap.clone()).subMap(startTime, true, endTime, true);
        return subMap;
    }
}
