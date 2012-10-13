package de.minestar.diehard.core;

public class Time implements Comparable<Time> {
    private int hours;
    private int minutes;

    public Time(int h, int m) {
        setTime(h, m);
    }

    public Time(int m) {
        int h = m / 60;
        m %= 60;
        setTime(h, m);
    }

    public Time(String timeText) {
        String[] timeFragments = timeText.split(":");
        int hours, minutes;
        try {
            hours = Integer.parseInt(timeFragments[0]);
            minutes = Integer.parseInt(timeFragments[1]);

            setTime(hours, minutes);
        } catch (NumberFormatException e) {
            this.hours = -2;
            this.minutes = -2;
        }
    }

    public int compareTo(Time t) {
        if (this.hours == t.hours) {
            if (this.minutes == t.minutes) {
                return 0;
            } else {
                return this.minutes - t.minutes;
            }
        } else {
            return this.hours - t.hours;
        }
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", this.hours, this.minutes);
    }

    public Time substract(Time t) {
        int hours = this.hours - t.hours;
        int minutes = this.minutes - t.minutes;
        if (minutes < 0) {
            minutes = 60 + minutes;
            hours -= 1;
        }
        return new Time(hours, minutes);
    }

    public Time add(Time t) {
        int h = this.hours + t.hours;
        int m = this.minutes + t.minutes;
        if (m > 59) {
            m -= 60;
            h += 1;
        }
        System.out.println("hours = " + h);
        System.out.println("minutes = " + m);
        return new Time(h, m);
    }

    public int toMinutes() {
        return this.hours * 60 + this.minutes;
    }

    private void setTime(int h, int m) {
        if (this.isValid(h, m)) {
            this.hours = h;
            this.minutes = m;
        } else {
            this.hours = -1;
            this.minutes = -1;
        }
    }

    private boolean isValid(int hours, int minutes) {
        if ((hours >= 0) && (hours <= 23) && (minutes >= 0) && (minutes <= 59)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isValid() {
        return this.isValid(this.hours, this.minutes);
    }

    public boolean isGreater(Time t) {
        if (this.hours > t.hours) {
            return true;
        } else if (this.hours < t.hours) {
            return false;
        } else {
            if (this.minutes > t.minutes) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean equals(Time t) {
        if ((this.hours == t.hours) && (this.minutes == t.minutes)) {
            return true;
        } else {
            return false;
        }
    }
}
