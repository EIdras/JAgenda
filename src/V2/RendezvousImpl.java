package V2;

import myrendezvous.Rendezvous;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class RendezvousImpl implements Rendezvous, Cloneable {

    Calendar time;
    int duration;
    String title;
    String description;
    TurboTag tag;

    public RendezvousImpl(Calendar time, int duration, String title) {
        this.tag = new TurboTag((Calendar) time.clone(), UUID.randomUUID());
        this.time = time;
        this.duration = duration;
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

    public TurboTag getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RendezvousImpl that = (RendezvousImpl) o;
        return duration == that.duration && time.equals(that.time) && title.equals(that.title) && Objects.equals(description, that.description) && tag.equals(that.tag);
    }

}
