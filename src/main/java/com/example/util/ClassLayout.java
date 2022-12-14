/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.example.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.SortedSet;

import org.openjdk.jol.info.ClassData;
import org.openjdk.jol.info.FieldLayout;
import org.openjdk.jol.util.ClassUtils;
import org.openjdk.jol.util.ObjectUtils;
import org.openjdk.jol.vm.VM;
import org.openjdk.jol.vm.VirtualMachine;

/**
 * Handles the class data *with* the layout information.
 * 
 * @author hxb
 */
public class ClassLayout {

    /**
     * Produce the class layout for the given class.
     *
     * This is a shortcut for
     * {@link #parseClass(Class,org.openjdk.jol.layouters.Layouter)},
     * but with a default layouter.
     *
     * @param klass class to work on
     * @return class layout
     */
    public static ClassLayout parseClass(Class<?> klass) {
        return parseClass(klass, new CurrentLayouter());
    }

    /**
     * Produce the class layout for the given class, and given layouter.
     *
     * Note: this method is usable as the "caching" shortcut for
     * {@link #parseInstance(Object)}.
     * You can use it to cache the introspection results for a constant-sized
     * objects, e.g. plain Java objects. It is not recommended to use this method
     * on arrays, since their lengths differ from instance to instance.
     *
     * @param klass    class to work on
     * @param layouter class layouter
     * @return class layout
     */
    public static ClassLayout parseClass(Class<?> klass, Layouter layouter) {
        return layouter.layout(ClassData.parseClass(klass));
    }

    /**
     * Produce the class layout for the given instance.
     *
     * This is a shortcut for
     * {@link #parseInstance(java.lang.Object,org.openjdk.jol.layouters.Layouter)},
     * but with a default layouter.
     *
     * @param instance instance to work on
     * @return class layout
     */
    public static ClassLayout parseInstance(Object instance) {
        return parseInstance(instance, new CurrentLayouter());
    }

    /**
     * Produce the class layout for the given instance, and given layouter.
     *
     * These methods, along with {@link #parseInstance(Object)} are recommended
     * for use when the shape of the object is not known in advance. For example,
     * variable-sized instances (e.g. Java arrays) would not be parsed by
     * {@link #parseClass(Class)} properly, because their lengths are encoded in
     * the instance objects, not in classes.
     *
     * @param instance instance to work on
     * @param layouter class layouter
     * @return class layout
     */
    public static ClassLayout parseInstance(Object instance, Layouter layouter) {
        return layouter.layout(ClassData.parseInstance(instance));
    }

    private final ClassData classData;
    private final SortedSet<FieldLayout> fields;
    private final int headerSize;
    private final long size;
    private final int lossesInternal;
    private final int lossesExternal;
    private final int lossesTotal;

    private ClassLayout(ClassData classData, SortedSet<FieldLayout> fields, int headerSize, long instanceSize,
            int lossesInternal, int lossesExternal, int lossesTotal) {
        this.classData = classData;
        this.fields = fields;
        this.headerSize = headerSize;
        this.size = instanceSize;
        this.lossesInternal = lossesInternal;
        this.lossesExternal = lossesExternal;
        this.lossesTotal = lossesTotal;
    }

    /**
     * Builds the class layout.
     *
     * @param classData    class data
     * @param fields       field layouts
     * @param headerSize   header size
     * @param instanceSize instance size
     * @param check        whether to check important invariants
     * @return a new instance of the ClassLayout
     */
    public static ClassLayout create(ClassData classData, SortedSet<FieldLayout> fields, int headerSize,
            long instanceSize, boolean check) {
        if (check) {
            checkInvariants(fields, instanceSize);
        }
        // calculate loses
        long next = headerSize;
        long internal = 0;
        for (FieldLayout fl : fields) {
            if (fl.offset() > next) {
                internal += fl.offset() - next;
            }
            next = fl.offset() + fl.size();
        }
        long external = (instanceSize != next) ? (instanceSize - next) : 0;
        long total = internal + external;
        return new ClassLayout(classData, fields, headerSize, instanceSize, (int) internal, (int) external,
                (int) total);
    }

    private static void checkInvariants(SortedSet<FieldLayout> fields, long instanceSize) {
        FieldLayout lastField = null;
        for (FieldLayout f : fields) {
            if (f.offset() % f.size() != 0) {
                throw new IllegalStateException("Field " + f + " is not aligned");
            }
            if (f.offset() + f.size() > instanceSize) {
                throw new IllegalStateException("Field " + f + " is overflowing the object of size " + instanceSize);
            }
            if (lastField != null && (f.offset() < lastField.offset() + lastField.size())) {
                throw new IllegalStateException("Field " + f + " overlaps with the previous field " + lastField);
            }
            lastField = f;
        }
    }

    /**
     * Answer the set of fields, including those in superclasses
     *
     * @return sorted set of fields
     */
    public SortedSet<FieldLayout> fields() {
        return fields;
    }

    /**
     * Answer instance size
     *
     * @return instance size
     */
    public long instanceSize() {
        return size;
    }

    /**
     * Answer header size
     *
     * @return header size
     */
    public int headerSize() {
        return headerSize;
    }

    /**
     * Loosed bytes from padding between fields
     *
     * @return Internally loosed bytes
     */
    public long getLossesInternal() {
        return lossesInternal;
    }

    /**
     * Loosed bytes due to next object alignment
     *
     * @return Externally loosed bytes
     */
    public long getLossesExternal() {
        return lossesExternal;
    }

    /**
     * Total loosed bytes i.e. lossesInternal + lossesExternal
     *
     * @return Total loosed bytes
     */
    public long getLossesTotal() {
        return lossesTotal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (FieldLayout f : fields()) {
            sb.append(f).append("\n");
        }
        sb.append("size = ").append(size).append("\n");
        return sb.toString();
    }

    /**
     * Produce printable stringly representation of class layout.
     * This method uses the instance originally provided to
     * {@link #parseInstance(Object)},
     * if that instance is still available.
     *
     * @return human-readable layout info
     */
    public String toPrintable() {
        return toPrintable(classData.instance());
    }

    /**
     * Produce printable stringly representation of class layout.
     * This method accepts instance to read the actual data from.
     *
     * @param instance instance to work on
     * @return human-readable layout info
     */
    public String toPrintable(Object instance) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        String MSG_GAP = "(alignment/padding gap)";
        String MSG_NEXT_GAP = "(loss due to the next object alignment)";

        int maxTypeLen = "TYPE".length();
        for (FieldLayout f : fields()) {
            maxTypeLen = Math.max(f.typeClass().length(), maxTypeLen);
        }
        maxTypeLen += 2;

        int maxDescrLen = Math.max(MSG_GAP.length(), MSG_NEXT_GAP.length());
        for (FieldLayout f : fields()) {
            maxDescrLen = Math.max(f.shortFieldName().length(), maxDescrLen);
        }
        maxDescrLen += 2;

        if (instance != null) {
            try {
                Class<?> klass = ClassUtils.loadClass(classData.name());
                if (!klass.isAssignableFrom(instance.getClass())) {
                    throw new IllegalArgumentException(
                            "Passed instance type " + instance.getClass() + " is not assignable from " + klass + ".");
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Class is not found: " + classData.name() + ".");
            }
        }
        if (instance != null) {
            VirtualMachine vm = VM.current();
            int word = vm.getInt(instance, 0);
            pw.printf(
                    toBinary((word >> 0) & 0xFF) + " " +
                            toBinary((word >> 8) & 0xFF) + " " +
                            toBinary((word >> 16) & 0xFF) + " " +
                            toBinary((word >> 24) & 0xFF));
        } else {
            pw.printf(" %6d %5d %" + maxTypeLen + "s %-" + maxDescrLen + "s %s%n", 0, headerSize(), "",
                    "(object header)", "N/A");
        }

        pw.close();

        return sw.toString();
    }

    // very ineffective, so what?
    private static String toBinary(int x) {
        String s = Integer.toBinaryString(x);
        int deficit = 8 - s.length();
        for (int c = 0; c < deficit; c++) {
            s = "0" + s;
        }
        return s;
    }

    // very ineffective, so what?
    private static String toHex(int x) {
        String s = Integer.toHexString(x);
        int deficit = 2 - s.length();
        for (int c = 0; c < deficit; c++) {
            s = "0" + s;
        }
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClassLayout that = (ClassLayout) o;

        if (headerSize != that.headerSize) {
            return false;
        }
        if (size != that.size) {
            return false;
        }
        return fields.equals(that.fields);

    }

    @Override
    public int hashCode() {
        int result = fields.hashCode();
        result = 31 * result + headerSize;
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }
}
