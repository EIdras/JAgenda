package V2;

import java.util.Calendar;
import java.util.UUID;

public class UidTag implements Comparable<UidTag>{
    private Calendar calendar;
    private UUID uuid;

    public UidTag(Calendar calendar) {
        this.calendar = calendar;
    }
    public UidTag(Calendar calendar, UUID uuid) {
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
    public int compareTo(UidTag tag) {

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
