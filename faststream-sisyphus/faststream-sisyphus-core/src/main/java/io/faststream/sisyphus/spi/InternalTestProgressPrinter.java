/*
 * Copyright (c) 2008 Kasper Nielsen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.faststream.sisyphus.spi;

import java.io.Serializable;
import java.text.DecimalFormatSymbols;
import java.util.concurrent.TimeUnit;

/**
 * A simple class that print the test progress.
 *
 * @author Kasper Nielsen
 */
class InternalTestProgressPrinter extends Thread {

    /** The total number of dots that should be printed out. */
    private static final int DOTS = 50;

    private int count;

    private double nextStep;
    private final InternalTestResult result;
    private long start;
    private final double stepSize;

    private final long total;

    InternalTestProgressPrinter(InternalTestResult result) {
        this.result = result;
        this.total = result.maximumNumberOfiterations;
        nextStep = stepSize = total / (double) DOTS;
    }

    void finished() {
        long stop = System.nanoTime();
        System.out.println();
        long dur = stop - start;
        long avgNs = dur / total;
        // InternalCountableTestSet<String> cs = new InternalCountableTestSet<>();
        // for (InternalRandomTestThreadRunner r : result.runners) {
        // cs.add(r.executedTests);
        // }
        // System.out.println(cs.toSortedString());
        System.out.println(
                "Total time " + InternalDurationFormatter.DEFAULT.formatNanos(dur) + ", avg time = " + InternalDurationFormatter.DEFAULT.formatNanos(avgNs));
    }

    @Override
    public void run() {
        for (;;) {
            if (runFinished() || result.hasFailures()) {
                return;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignore) {}
        }
    }

    boolean runFinished() {
        long runid = 0;
        for (BatchRunner r : result.runners) {
            runid += r.operationsFinished.get();
            // System.out.println(r.operationsFinished);
        }
        // System.out.println("----");
        while (runid >= nextStep) {
            System.out.print(".");
            nextStep += stepSize;
            count++;
        }
        if (runid == total) {
            while (count++ < DOTS) {
                System.out.print(".");
            }
            finished();
            return true;
        }
        return false;
    }

    public void start() {
        start = System.nanoTime();
        System.out.println("Running " + total + " steps");
        System.out.println("--------------------------------------------------");
        super.start();
    }

    static class InternalDurationFormatter implements Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -1806478460924436710L;

        /** The platforms decimal separator. */
        private static final char DECIMAL_SEPARATOR = new DecimalFormatSymbols().getDecimalSeparator();

        /** nanoseconds per microsecond. */
        private static final long MC_S = 1000L;

        /** nanoseconds per millisecond. */
        private static final long MS = MC_S * 1000L;

        /** nanoseconds per second. */
        private static final long S = MS * 1000L;

        /** nanoseconds per minute. */
        private static final long M = S * 60;

        /** nanoseconds per hour. */
        private static final long H = M * 60;

        /** nanoseconds per day. */
        private static final long D = H * 24;

        /** The singular name of the time unit. */
        private static final String[] NAME = new String[] { "nanosecond", "microsecond", "millisecond", "second", "minute", "hour", "day" };

        /** The plural name of the time unit. */
        private static final String[] NAMES = new String[] { "nanoseconds", "microseconds", "milliseconds", "seconds", "minutes", "hours", "days" };

        /** The default SI symbols. */
        static final String[] SI_SYMBOL = new String[] { "ns", "us", "ms", "s", "min", "h", "d" };

        /** The default time formatter. */
        public static final InternalDurationFormatter DEFAULT = new DefaultFormatter();

        /** A <tt>TimeFormatter</tt> that will format a duration in the same way as the unix 'uptime' command. */
        public static final InternalDurationFormatter UPTIME = new UptimeFormatter();

        /**
         * Formats the specified time parameter.
         *
         * @param nano
         *            the nanoseconds part
         * @return the formatted string
         */
        protected String doFormat(int nano) {
            return doFormat(0, nano);
        }

        /**
         * Formats the specified time parameters.
         *
         * @param micros
         *            the microseconds part
         * @param nano
         *            the nanoseconds part
         *
         * @return the formatted string
         */
        protected String doFormat(int micros, int nano) {
            return doFormat(0, micros, nano);
        }

        /**
         * Formats the specified time parameters.
         *
         * @param millies
         *            the milliseconds part
         * @param micros
         *            the microseconds part
         * @param nano
         *            the nanoseconds part
         *
         * @return the formatted string
         */
        protected String doFormat(int millies, int micros, int nano) {
            return doFormat(0, millies, micros, nano);
        }

        /**
         * Formats the specified time parameters.
         *
         * @param seconds
         *            the seconds part
         * @param millies
         *            the milliseconds part
         * @param micros
         *            the microseconds part
         * @param nano
         *            the nanoseconds part
         *
         * @return the formatted string
         */
        protected String doFormat(int seconds, int millies, int micros, int nano) {
            return doFormat(0, seconds, millies, micros, nano);
        }

        /**
         * Formats the specified time parameters.
         *
         * @param minutes
         *            the minutes part
         * @param seconds
         *            the seconds part
         * @param millies
         *            the milliseconds part
         * @param micros
         *            the microseconds part
         * @param nano
         *            the nanoseconds part
         *
         * @return the formatted string
         */
        protected String doFormat(int minutes, int seconds, int millies, int micros, int nano) {
            return doFormat(0, minutes, seconds, millies, micros, nano);
        }

        /**
         * Formats the specified time parameters.
         *
         * @param hours
         *            the hours part
         * @param minutes
         *            the minutes part
         * @param seconds
         *            the seconds part
         * @param millies
         *            the milliseconds part
         * @param micros
         *            the microseconds part
         * @param nano
         *            the nanoseconds part
         *
         * @return the formatted string
         */
        protected String doFormat(int hours, int minutes, int seconds, int millies, int micros, int nano) {
            return doFormat(0, hours, minutes, seconds, millies, micros, nano);
        }

        /**
         * Formats the specified time parameters.
         *
         * @param days
         *            the days part
         * @param hours
         *            the hours part
         * @param minutes
         *            the minutes part
         * @param seconds
         *            the seconds part
         * @param millies
         *            the milliseconds part
         * @param micros
         *            the microseconds part
         * @param nano
         *            the nanoseconds part
         *
         * @return the formatted string
         */
        protected String doFormat(int days, int hours, int minutes, int seconds, int millies, int micros, int nano) {
            return days + " day(s), " + format00(hours) + ":" + format00(minutes) + ":" + format00(seconds + (millies >= 500 ? 1 : 0)) + " hours";
        }

        /**
         * Formats the specified time to produce a string.
         *
         * @param time
         *            the amount of time
         * @param unit
         *            the unit of the specified time
         * @return the formatting string
         */
        public String format(long time, TimeUnit unit) {
            return formatNanos(unit.toNanos(time));
        }

        /**
         * Formats the specified time to produce a string.
         *
         * @param millies
         *            the amount of time in milliseconds
         * @return the formatting string
         */
        public String formatMillies(long millies) {
            return formatNanos(millies * MS);
        }

        /**
         * Formats the specified time to produce a string.
         *
         * @param nanos
         *            the amount of time in nanos
         * @return the formatting string
         */
        public String formatNanos(long nanos) {
            if (nanos < MC_S) {
                return doFormat((int) nanos);
            } else if (nanos < MS) {
                return doFormat((int) (nanos / MC_S), (int) (nanos % 1000));
            } else if (nanos < S) {
                return doFormat((int) (nanos / MS), (int) (nanos / MC_S % 1000), (int) (nanos % 1000));
            } else if (nanos < M) {
                return doFormat((int) (nanos / S), (int) (nanos / MS % 1000), (int) (nanos / MC_S % 1000), (int) (nanos % 1000));
            } else if (nanos < H) {
                return doFormat((int) (nanos / M), (int) (nanos / S % 60), (int) (nanos / MS % 1000), (int) (nanos / MC_S % 1000), (int) (nanos % 1000));
            } else if (nanos < D) {
                return doFormat((int) (nanos / H), (int) (nanos / M % 60), (int) (nanos / S % 60), (int) (nanos / MS % 1000), (int) (nanos / MC_S % 1000),
                        (int) (nanos % 1000));
            }
            return doFormat((int) (nanos / D), (int) (nanos / H % 24), (int) (nanos / M % 60), (int) (nanos / S % 60), (int) (nanos / MS % 1000),
                    (int) (nanos / MC_S % 1000), (int) (nanos % 1000));
        }

        /**
         * Returns name of the specified time unit. For example getName(1, TimeUnit.seconds) will return 'second' while
         * invoking getName(10, TimeUnit.seconds) will return 'seconds'.
         *
         * @param value
         *            the amount of time
         * @param unit
         *            the time unit
         * @return the name of the specified time unit
         */
        protected String getName(long value, TimeUnit unit) {
            return value == 1 ? NAME[unit.ordinal()] : NAMES[unit.ordinal()];
        }

        /**
         * Returns the symbol of the specified time unit. The default implementation returns the standard SI symbol for
         * the specified time unit.
         *
         * @param unit
         *            the time unit for which the symbol should be returned
         * @return the symbol of the specified time unit
         */
        protected String getSymbol(TimeUnit unit) {
            return SI_SYMBOL[unit.ordinal()];
        }

        /**
         * Formats the specified values into a <tt>integer-part{decimal separator}decimal-part</tt> format.
         *
         * @param value
         *            the integer part
         * @param decimal
         *            the decimal part
         * @return the numbers formatted
         */
        protected String format_000(int value, int decimal) {
            return new StringBuilder().append(value).append(DECIMAL_SEPARATOR).append(format000(decimal)).toString();
        }

        private String format00(int value) {
            if (value < 10) {
                return "0" + value;
            }
            return Integer.toString(value);
        }

        private String format000(int value) {
            if (value < 10) {
                return "00" + value;
            } else if (value < 100) {
                return "0" + value;
            }
            return Integer.toString(value);
        }

        /** The default time formatter. */
        static class DefaultFormatter extends UptimeFormatter {
            /** serialVersionUID */
            private static final long serialVersionUID = -7573098942957592504L;

            /** {@inheritDoc} */
            @Override
            protected String doFormat(int nano) {
                return nano + " " + SI_SYMBOL[0];
            }

            /** {@inheritDoc} */
            @Override
            protected String doFormat(int micros, int nano) {
                return format_000(micros, nano) + " " + SI_SYMBOL[1];
            }

            /** {@inheritDoc} */
            @Override
            protected String doFormat(int millies, int micros, int nano) {
                return format_000(millies, micros) + " " + SI_SYMBOL[2];
            }

            /** {@inheritDoc} */
            @Override
            protected String doFormat(int seconds, int millies, int micros, int nano) {
                return format_000(seconds, millies) + " s";
            }
        }

        /** Time formatter equivalent to *nix 'uptime' command. */
        static class UptimeFormatter extends InternalDurationFormatter {
            /** serialVersionUID */
            private static final long serialVersionUID = 3440524099536856811L;
        }
    }

}
