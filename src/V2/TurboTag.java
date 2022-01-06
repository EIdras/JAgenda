package V2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class TurboTag implements Comparable<TurboTag>{
    private Calendar calendar;
    private UUID uuid;

    public TurboTag(Calendar calendar) {
        this.calendar = calendar;
    }
    public TurboTag(Calendar calendar, UUID uuid) {
        this.calendar = calendar;
        this.uuid = uuid;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public UUID getUuid() {
        return uuid;
    }


    // Renvoie le résultat de compareTo sur l'objet Calendar
    // Si les deux Calendar sont égaux, renvoie compareTo sur l'UUID
    @Override
    public int compareTo(TurboTag tag) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        //System.out.println("Je compare "+dateFormat.format(this.getCalendar().getTime()));
        //System.out.println("Et         "+dateFormat.format(tag.getCalendar().getTime()));

        int comparisonResult = this.getCalendar().compareTo(tag.getCalendar());
        if (comparisonResult == 0){
            if (tag.getUuid() == null || uuid == null){
                return 0;
            } else{
                comparisonResult = this.getUuid().compareTo(tag.getUuid());
            }
        }
        return comparisonResult;
    }
}
