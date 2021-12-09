package main;

import myrendezvous.Rendezvous;
import myrendezvous.RendezvousManager;
import myrendezvous.exceptions.RendezvousNotFound;

import java.util.*;

public class RendezvousManagerImpl implements RendezvousManager {

    TreeMap<Calendar, RendezvousImpl> treeMap = new TreeMap<>();

    public TreeMap<Calendar, RendezvousImpl> getTreeMap() {
        return treeMap;
    }

    @Override
    public Rendezvous addRendezvous(Rendezvous rdv) {
            // Copie le rendez-vous passé en paramètre
            RendezvousImpl rdvToAdd = new RendezvousImpl(rdv.getTime(), rdv.getDuration(), rdv.getTitle(), rdv.getDescription());
            RendezvousImpl rdvToClone = rdvToAdd.clone();
            // Sauvegarde la copie dans sa structure interne
            treeMap.put(rdvToClone.getTag(), rdvToClone);
            // Créée une copie de la structure et y ajoute un tagging (ensemble d'attributs immuables)
            return rdvToClone.clone();
        /*
        try {
            main.RendezvousImpl add = new main.RendezvousImpl(arg0.getTime(), arg0.getDuration(), arg0.getTitle(),arg0.getDescription());
            main.RendezvousImpl instruct = add.clone();
            gestionnaire.put(instruct.getKey(), instruct);
            return instruct.clone();

        } catch (CloneNotSupportedException e) {
        }
        return null;
         */
    }

    @Override
    public void removeRendezvous(Rendezvous rendezvous) throws IllegalArgumentException, RendezvousNotFound {

    }

    @Override
    public boolean removeRendezvous(Calendar calendar) {
        return false;
    }

    @Override
    public void removeAllRendezvousBefore(Calendar calendar) throws IllegalArgumentException {
        for(Map.Entry<Calendar, RendezvousImpl> entry : treeMap.entrySet()) {
            // Si la valeur de l'instant de départ de l'argument se situe avant
            // la valeur de l'instant de départ de l'entrée
            if ((entry.getValue().getTime().getTime().compareTo(calendar.getTime())) >= 0){
                treeMap.remove(entry.getKey());
            }
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
            // la valeur de l'instant de départ des deux paramètres
            if ((entry.getValue().getTime().getTime().compareTo(startTime.getTime())) <= 0
                && (entry.getValue().getTime().getTime().compareTo(endTime.getTime())) >= 0){
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
            if ((entry.getValue().getTime().getTime().compareTo(calendar.getTime())) >= 0){
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
            if ((entry.getValue().getTime().getTime().compareTo(calendar.getTime())) <= 0){
                // On ajoute une copie de l'entrée à la liste
                rendezvousList.add(((treeMap.get(entry.getKey()))).clone());
            }
        }
        return rendezvousList;
    }

    @Override
    public List<Rendezvous> getRendezvousToday() {

        Calendar today = Calendar.getInstance();
        List<Rendezvous> rendezvousList = new ArrayList<>();
        for(Map.Entry<Calendar, RendezvousImpl> entry : treeMap.entrySet()) {
            if (entry.getValue().getTime().equals(today)){
                rendezvousList.add(treeMap.get(entry));
            }
        }
        return rendezvousList;
    }

    @Override
    public boolean hasOverlap(Calendar calendar, Calendar calendar1) {
        return false;
    }

    @Override
    public Calendar findFreeTime(int i, Calendar calendar, Calendar calendar1) throws IllegalArgumentException {
        return null;
    }

    @Override
    public List<Rendezvous> findRendezvousByTitleEqual(String s, Calendar calendar, Calendar calendar1) {
        return null;
    }

    @Override
    public List<Rendezvous> findRendezvousByTitleALike(String s, Calendar calendar, Calendar calendar1) {
        return null;
    }
}
