import com.sun.source.tree.Tree;
import myrendezvous.Rendezvous;
import myrendezvous.RendezvousManager;
import myrendezvous.exceptions.RendezvousNotFound;

import java.util.*;

public class RendezvousManagerImpl implements RendezvousManager {

    TreeMap<Calendar, Rendezvous> treeMap;


    private String generateKey() {
        return UUID.randomUUID().toString();
    }


    @Override
    public Rendezvous addRendezvous(Rendezvous rendezvous) {
        // Copie le rendez-vous passé en paramètre
        Rendezvous rdv = ((RendezvousImpl) rendezvous).clone();
        // Sauvegarde la copie dans sa structure interne
        treeMap.put(rendezvous.getTime(),rdv);
        // Créée une copie de la structure et y ajoute un tagging (ensemble d'attributs immuables)

        return null;
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

    }

    @Override
    public Rendezvous updateRendezvous(Rendezvous rendezvous) throws RendezvousNotFound {
        return null;
    }

    @Override
    public List<Rendezvous> getRendezvousBetween(Calendar calendar, Calendar calendar1) throws IllegalArgumentException {
        return null;
    }

    @Override
    public List<Rendezvous> getRendezvousBefore(Calendar calendar) throws IllegalArgumentException {
        return null;
    }

    @Override
    public List<Rendezvous> getRendezvousAfter(Calendar calendar) throws IllegalArgumentException {
        List<Rendezvous> rendezvousList = new ArrayList<>();
        for(Map.Entry<Calendar, Rendezvous> entry : treeMap.entrySet()) {
            // Si la valeur de l'instant de départ de l'argument se situe après
            // la valeur de l'instant de départ de l'entrée
            if ((entry.getValue().getTime().getTime().compareTo(calendar.getTime())) <= 0){
                // On ajoute une copie de l'entrée à la liste
                rendezvousList.add(((RendezvousImpl)(treeMap.get(entry.getKey()))).clone());
            }
        }
        return rendezvousList;
    }

    @Override
    public List<Rendezvous> getRendezvousToday() {

        Calendar today = Calendar.getInstance();
        List<Rendezvous> rendezvousList = new ArrayList<>();
        for(Map.Entry<Calendar, Rendezvous> entry : treeMap.entrySet()) {
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
