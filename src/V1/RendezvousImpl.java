package V1;

import myrendezvous.Rendezvous;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class RendezvousImpl implements Rendezvous, Cloneable {

    Calendar time;
    int duration;
    String title;
    String description;
    Calendar tag;

    public RendezvousImpl(Calendar time, int duration, String title) {
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        this.tag = (Calendar) time.clone();
        this.time = time;
        this.duration = (duration == 0) ? 1 : Math.abs(duration); // Gère les durées négatives et nulles
        this.title = title;
    }

    public RendezvousImpl(Calendar time, int duration, String title, String description) {
        this(time, duration, title);
        this.description = description;
    }

    @Override
    protected RendezvousImpl clone() {
        try {
            RendezvousImpl rdvCopy = (RendezvousImpl) super.clone();
            rdvCopy.time = (Calendar) this.time.clone();
            return rdvCopy;
        } catch (CloneNotSupportedException e){
            throw new IllegalStateException();
        }
    }

    @Override
    public Calendar getTime() {
        return time;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setTime(Calendar calendar) throws IllegalArgumentException {
        this.time = calendar;
    }

    @Override
    public void setDuration(int i) throws IllegalArgumentException {
        this.duration = i;
    }

    @Override
    public void setTitle(String s) throws IllegalArgumentException {
        this.title = s;
    }

    @Override
    public void setDescription(String s) {
        this.description = s;
    }

    public Calendar getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RendezvousImpl that = (RendezvousImpl) o;
        return duration == that.duration && time.equals(that.time) && title.equals(that.title) && Objects.equals(description, that.description) && tag.equals(that.tag);
    }


    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return  "\"" + title + "\" : " + dateFormat.format(time.getTime())
                + " - " + duration + " min"
                + ((description == null)? "" : "\n(\""+ description +"\")");

    }

}
