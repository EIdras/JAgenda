package main;

import myrendezvous.Rendezvous;

import java.util.Calendar;

public class RendezvousImpl implements Rendezvous, Cloneable {

    Calendar time;
    int duration;
    String title;
    String description;
    Calendar tag;

    public RendezvousImpl(Calendar time, int duration, String title, String description) {
        this(time, duration, title);
        this.description = description;
    }

    public RendezvousImpl(Calendar time, int duration, String title) {
        this.tag = (Calendar) time.clone();
        this.time = time;
        this.duration = duration;
        this.title = title;
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
}