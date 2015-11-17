package com.boxofc.mdag;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public class DAWGMap extends AbstractMap<String, String> implements NavigableMap<String, String> {
    private static final char KEY_VALUE_SEPARATOR = '\0';
    private static final char KEY_VALUE_SEPARATOR_EXCLUSIVE = '\1';
    DAWGSet dawg;
    
    DAWGMap() {
    }
    
    DAWGMap(DAWGSet dawg) {
        this.dawg = dawg;
    }

    @Override
    public int size() {
        return dawg.size();
    }

    @Override
    public boolean isEmpty() {
        return dawg.isEmpty();
    }
    
    private static void checkNotNullAndContainsNoZeros(Object o) {
        if (o == null)
            throw new NullPointerException();
        if (((String)o).indexOf(KEY_VALUE_SEPARATOR) >= 0)
            throw new IllegalArgumentException("Argument contains zero character");
    }
    
    private static String getFirstElement(Iterable<String> i) {
        for (String s : i)
            return s;
        return null;
    }
    
    private static String pollFirstElement(Iterable<String> i) {
        for (Iterator<String> it = i.iterator(); it.hasNext();) {
            String s = it.next();
            it.remove();
            return s;
        }
        return null;
    }
    
    private static String valueOfStringEntry(String stringEntry, Object key) {
        return stringEntry == null ? null : stringEntry.substring(((String)key).length() + 1);
    }
    
    private static String valueOfStringEntry(String stringEntry) {
        return stringEntry == null ? null : stringEntry.substring(stringEntry.indexOf(KEY_VALUE_SEPARATOR) + 1);
    }
    
    private static String keyOfStringEntry(String stringEntry) {
        return stringEntry == null ? null : stringEntry.substring(0, stringEntry.indexOf(KEY_VALUE_SEPARATOR));
    }
    
    private Entry<String, String> entryOfStringEntry(String stringEntry) {
        if (stringEntry == null)
            return null;
        int idx = stringEntry.indexOf(KEY_VALUE_SEPARATOR);
        return new MapEntry(stringEntry.substring(0, idx), stringEntry.substring(idx + 1));
    }
    
    private boolean containsEntry(Entry<String, String> e) {
        checkNotNullAndContainsNoZeros(e.getKey());
        checkNotNullAndContainsNoZeros(e.getValue());
        return dawg.contains(e.getKey() + KEY_VALUE_SEPARATOR + e.getValue());
    }

    @Override
    public boolean containsKey(Object key) {
        checkNotNullAndContainsNoZeros(key);
        return dawg.getStringsStartingWith((String)key + KEY_VALUE_SEPARATOR).iterator().hasNext();
    }

    @Override
    public boolean containsValue(Object value) {
        checkNotNullAndContainsNoZeros(value);
        return dawg.getStringsEndingWith(KEY_VALUE_SEPARATOR + (String)value).iterator().hasNext();
    }
    
    private boolean removeValue(Object value) {
        checkNotNullAndContainsNoZeros(value);
        Iterator<String> it = dawg.getStringsEndingWith(KEY_VALUE_SEPARATOR + (String)value).iterator();
        if (it.hasNext()) {
            String stringEntry = it.next();
            return dawg.remove(stringEntry);
        }
        return false;
    }

    @Override
    public String get(Object key) {
        checkNotNullAndContainsNoZeros(key);
        return valueOfStringEntry(getFirstElement(dawg.getStringsStartingWith((String)key + KEY_VALUE_SEPARATOR)), key);
    }

    @Override
    public String put(String key, String value) {
        checkNotNullAndContainsNoZeros(value);
        String old = get(key);
        if (old != null && old.equals(value))
            return old;
        String keyWithSeparator = key + KEY_VALUE_SEPARATOR;
        dawg.add(keyWithSeparator + value);
        if (old != null)
            dawg.remove(keyWithSeparator + old);
        return old;
    }

    @Override
    public String remove(Object key) {
        checkNotNullAndContainsNoZeros(key);
        return valueOfStringEntry(pollFirstElement(dawg.getStringsStartingWith((String)key + KEY_VALUE_SEPARATOR)), key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        checkNotNullAndContainsNoZeros(key);
        checkNotNullAndContainsNoZeros(value);
        return dawg.remove((String)key + KEY_VALUE_SEPARATOR + value);
    }

    @Override
    public void clear() {
        dawg.clear();
    }

    @Override
    public Entry<String, String> lowerEntry(String key) {
        checkNotNullAndContainsNoZeros(key);
        return entryOfStringEntry(dawg.lower(key + KEY_VALUE_SEPARATOR));
    }

    @Override
    public String lowerKey(String key) {
        checkNotNullAndContainsNoZeros(key);
        return keyOfStringEntry(dawg.lower(key + KEY_VALUE_SEPARATOR));
    }

    @Override
    public Entry<String, String> floorEntry(String key) {
        checkNotNullAndContainsNoZeros(key);
        return entryOfStringEntry(dawg.lower(key + KEY_VALUE_SEPARATOR_EXCLUSIVE));
    }

    @Override
    public String floorKey(String key) {
        checkNotNullAndContainsNoZeros(key);
        return keyOfStringEntry(dawg.lower(key + KEY_VALUE_SEPARATOR_EXCLUSIVE));
    }

    @Override
    public Entry<String, String> ceilingEntry(String key) {
        checkNotNullAndContainsNoZeros(key);
        return entryOfStringEntry(dawg.ceiling(key + KEY_VALUE_SEPARATOR));
    }

    @Override
    public String ceilingKey(String key) {
        checkNotNullAndContainsNoZeros(key);
        return keyOfStringEntry(dawg.ceiling(key + KEY_VALUE_SEPARATOR));
    }

    @Override
    public Entry<String, String> higherEntry(String key) {
        checkNotNullAndContainsNoZeros(key);
        return entryOfStringEntry(dawg.ceiling(key + KEY_VALUE_SEPARATOR_EXCLUSIVE));
    }

    @Override
    public String higherKey(String key) {
        checkNotNullAndContainsNoZeros(key);
        return keyOfStringEntry(dawg.ceiling(key + KEY_VALUE_SEPARATOR_EXCLUSIVE));
    }

    @Override
    public Entry<String, String> firstEntry() {
        return entryOfStringEntry(dawg.first());
    }

    @Override
    public Entry<String, String> lastEntry() {
        return entryOfStringEntry(dawg.last());
    }

    @Override
    public String firstKey() {
        return keyOfStringEntry(dawg.first());
    }

    @Override
    public String lastKey() {
        return keyOfStringEntry(dawg.last());
    }

    public String pollFirstKey() {
        return keyOfStringEntry(dawg.pollFirst());
    }

    public String pollLastKey() {
        return keyOfStringEntry(dawg.pollLast());
    }

    @Override
    public Entry<String, String> pollFirstEntry() {
        return entryOfStringEntry(dawg.pollFirst());
    }

    @Override
    public Entry<String, String> pollLastEntry() {
        return entryOfStringEntry(dawg.pollLast());
    }

    @Override
    public Comparator<? super String> comparator() {
        // Natural ordering.
        return null;
    }

    public boolean isAlphabetOptimized() {
        return dawg.isAlphabetOptimized();
    }

    @Override
    public Collection<String> values() {
        return new Values(this);
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return new EntrySet(this);
    }

    @Override
    public NavigableSet<String> keySet() {
        return new KeySet(this, false);
    }

    @Override
    public NavigableSet<String> navigableKeySet() {
        return new KeySet(this, false);
    }

    @Override
    public NavigableSet<String> descendingKeySet() {
        return new KeySet(this, true);
    }

    @Override
    public NavigableMap<String, String> descendingMap() {
        return new SubMap(dawg.descendingSet());
    }

    @Override
    public NavigableMap<String, String> subMap(String fromKey, boolean fromInclusive, String toKey, boolean toInclusive) {
        if (fromKey.compareTo(toKey) > 0)
            throw new IllegalArgumentException("fromKey > toKey");
        if (fromInclusive && fromKey.isEmpty())
            fromKey = null;
        else
            checkNotNullAndContainsNoZeros(fromKey);
        checkNotNullAndContainsNoZeros(toKey);
        return new SubMap(dawg.subSet(fromKey + (fromInclusive ? KEY_VALUE_SEPARATOR : KEY_VALUE_SEPARATOR_EXCLUSIVE), toKey + (toInclusive ? KEY_VALUE_SEPARATOR_EXCLUSIVE : KEY_VALUE_SEPARATOR)));
    }

    @Override
    public NavigableMap<String, String> headMap(String toKey, boolean inclusive) {
        checkNotNullAndContainsNoZeros(toKey);
        return new SubMap(dawg.headSet(toKey + (inclusive ? KEY_VALUE_SEPARATOR_EXCLUSIVE : KEY_VALUE_SEPARATOR)));
    }

    @Override
    public NavigableMap<String, String> tailMap(String fromKey, boolean inclusive) {
        if (inclusive && fromKey.isEmpty())
            return this;
        checkNotNullAndContainsNoZeros(fromKey);
        return new SubMap(dawg.tailSet(fromKey + (inclusive ? KEY_VALUE_SEPARATOR : KEY_VALUE_SEPARATOR_EXCLUSIVE)));
    }

    @Override
    public NavigableMap<String, String> subMap(String fromKey, String toKey) {
        if (fromKey.compareTo(toKey) > 0)
            throw new IllegalArgumentException("fromKey > toKey");
        if (fromKey.isEmpty())
            fromKey = null;
        else
            checkNotNullAndContainsNoZeros(fromKey);
        checkNotNullAndContainsNoZeros(toKey);
        return new SubMap(dawg.subSet(fromKey + KEY_VALUE_SEPARATOR, toKey + KEY_VALUE_SEPARATOR));
    }

    @Override
    public NavigableMap<String, String> headMap(String toKey) {
        checkNotNullAndContainsNoZeros(toKey);
        return new SubMap(dawg.headSet(toKey + KEY_VALUE_SEPARATOR));
    }

    @Override
    public NavigableMap<String, String> tailMap(String fromKey) {
        if (fromKey.isEmpty())
            return this;
        checkNotNullAndContainsNoZeros(fromKey);
        return new SubMap(dawg.tailSet(fromKey + KEY_VALUE_SEPARATOR));
    }
    
    public NavigableMap<String, String> prefixMap(String keyPrefix) {
        if (keyPrefix == null || keyPrefix.isEmpty())
            return this;
        checkNotNullAndContainsNoZeros(keyPrefix);
        return new SubMap(dawg.prefixSet(keyPrefix));
    }
    
    private Iterator<String> keyIterator(boolean desc) {
        return keyIterator(dawg, desc);
    }
    
    private Iterator<String> keyIterator(NavigableSet<String> set, boolean desc) {
        return new Iterator<String>() {
            private final Iterator<String> delegate = desc ? set.descendingIterator() : set.iterator();
            
            @Override
            public String next() {
                return keyOfStringEntry(delegate.next());
            }

            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public void remove() {
                delegate.remove();
            }
        };
    }
    
    private Iterator<Entry<String, String>> entryIterator() {
        return entryIterator(dawg);
    }
    
    private Iterator<Entry<String, String>> entryIterator(NavigableSet<String> set) {
        return new Iterator<Entry<String, String>>() {
            private final Iterator<String> delegate = set.iterator();
            
            @Override
            public Entry<String, String> next() {
                return entryOfStringEntry(delegate.next());
            }

            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public void remove() {
                delegate.remove();
            }
        };
    }
    
    private Iterator<String> valueIterator() {
        return valueIterator(dawg);
    }
    
    private Iterator<String> valueIterator(NavigableSet<String> set) {
        return new Iterator<String>() {
            private final Iterator<String> delegate = set.iterator();
            
            @Override
            public String next() {
                return valueOfStringEntry(delegate.next());
            }

            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public void remove() {
                delegate.remove();
            }
        };
    }
    
    private class MapEntry implements Entry<String, String> {
        private final String key;
        private String value;
        
        public MapEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            String old = put(key, value);
            this.value = value;
            return old;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!(obj instanceof Entry))
                return false;
            Entry e = (Entry)obj;
            return Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue());
        }

        @Override
        public String toString() {
            return key + '=' + value;
        }
    }
    
    private static class KeySet extends AbstractSet<String> implements NavigableSet<String> {
        private final NavigableMap<String, String> map;
        private final boolean desc;
        
        public KeySet(NavigableMap<String, String> map, boolean desc) {
            this.map = map;
            this.desc = desc;
        }

        @Override
        public String lower(String e) {
            return desc ? map.higherKey(e) : map.lowerKey(e);
        }

        @Override
        public String floor(String e) {
            return desc ? map.ceilingKey(e) : map.floorKey(e);
        }

        @Override
        public String ceiling(String e) {
            return desc ? map.floorKey(e) : map.ceilingKey(e);
        }

        @Override
        public String higher(String e) {
            return desc ? map.lowerKey(e) : map.higherKey(e);
        }

        @Override
        public String pollFirst() {
            Entry<String, String> e = desc ? map.pollLastEntry() : map.pollFirstEntry();
            return e == null ? null : e.getKey();
        }

        @Override
        public String pollLast() {
            Entry<String, String> e = desc ? map.pollFirstEntry() : map.pollLastEntry();
            return e == null ? null : e.getKey();
        }

        @Override
        public String first() {
            return desc ? map.lastKey() : map.firstKey();
        }

        @Override
        public String last() {
            return desc ? map.firstKey() : map.lastKey();
        }

        @Override
        public Iterator<String> iterator() {
            if (map instanceof DAWGMap)
                return ((DAWGMap)map).keyIterator(desc);
            else
                return ((SubMap)map).keyIterator(desc);
        }

        @Override
        public Iterator<String> descendingIterator() {
            if (map instanceof DAWGMap)
                return ((DAWGMap)map).keyIterator(!desc);
            else
                return ((SubMap)map).keyIterator(!desc);
        }

        @Override
        public NavigableSet<String> descendingSet() {
            return new KeySet(map, !desc);
        }

        @Override
        public NavigableSet<String> subSet(String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
            return new KeySet(map.subMap(fromElement, fromInclusive, toElement, toInclusive), desc);
        }

        @Override
        public NavigableSet<String> headSet(String toElement, boolean inclusive) {
            return new KeySet(map.headMap(toElement, inclusive), desc);
        }

        @Override
        public NavigableSet<String> tailSet(String fromElement, boolean inclusive) {
            return new KeySet(map.tailMap(fromElement, inclusive), desc);
        }

        @Override
        public SortedSet<String> subSet(String fromElement, String toElement) {
            return new KeySet(map.subMap(fromElement, true, toElement, false), desc);
        }

        @Override
        public SortedSet<String> headSet(String toElement) {
            return new KeySet(map.headMap(toElement, false), desc);
        }

        @Override
        public SortedSet<String> tailSet(String fromElement) {
            return new KeySet(map.tailMap(fromElement, true), desc);
        }

        @Override
        public Comparator<? super String> comparator() {
            return desc ? Collections.reverseOrder() : null;
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return map.containsKey((String)o);
        }

        @Override
        public boolean remove(Object o) {
            return map.remove((String)o) != null;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean ret = false;
            for (Object e : c)
                ret |= remove((String)e);
            return ret;
        }

        @Override
        public void clear() {
            map.clear();
        }
    }
    
    private static class EntrySet extends AbstractSet<Entry<String, String>> {
        private final NavigableMap<String, String> map;
        
        public EntrySet(NavigableMap<String, String> map) {
            this.map = map;
        }

        @Override
        public Iterator<Entry<String, String>> iterator() {
            if (map instanceof DAWGMap)
                return ((DAWGMap)map).entryIterator();
            else
                return ((SubMap)map).entryIterator();
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Entry))
                return false;
            Entry<String, String> e = (Entry<String, String>)o;
            if (map instanceof DAWGMap)
                return ((DAWGMap)map).containsEntry(e);
            else
                return ((SubMap)map).containsEntry(e);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Entry))
                return false;
            Entry<String, String> e = (Entry<String, String>)o;
            return map.remove(e.getKey(), e.getValue());
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean ret = false;
            for (Object e : c)
                ret |= remove(e);
            return ret;
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Override
        public boolean add(Entry<String, String> e) {
            String value = e.getValue();
            String old = map.put(e.getKey(), value);
            // Value is definitely not null (checked inside put).
            // Old may be null if this set did not contain the key before.
            // String.equals returns false when comparing to null.
            return !value.equals(old);
        }
    }
    
    private static class Values extends AbstractCollection<String> {
        private final NavigableMap<String, String> map;
        
        public Values(NavigableMap<String, String> map) {
            this.map = map;
        }

        @Override
        public Iterator<String> iterator() {
            if (map instanceof DAWGMap)
                return ((DAWGMap)map).valueIterator();
            else
                return ((SubMap)map).valueIterator();
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return map.containsValue((String)o);
        }

        @Override
        public boolean remove(Object o) {
            if (map instanceof DAWGMap)
                return ((DAWGMap)map).removeValue(o);
            else
                return ((SubMap)map).removeValue(o);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean ret = false;
            for (Object e : c)
                ret |= remove((String)e);
            return ret;
        }

        @Override
        public void clear() {
            map.clear();
        }
    }
    
    private class SubMap extends AbstractMap<String, String> implements NavigableMap<String, String> {
        private final NavigableSet<String> delegate;

        public SubMap(NavigableSet<String> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Entry<String, String> lowerEntry(String key) {
            checkNotNullAndContainsNoZeros(key);
            return entryOfStringEntry(delegate.lower(key + KEY_VALUE_SEPARATOR));
        }

        @Override
        public String lowerKey(String key) {
            checkNotNullAndContainsNoZeros(key);
            return keyOfStringEntry(delegate.lower(key + KEY_VALUE_SEPARATOR));
        }

        @Override
        public Entry<String, String> floorEntry(String key) {
            checkNotNullAndContainsNoZeros(key);
            return entryOfStringEntry(delegate.lower(key + KEY_VALUE_SEPARATOR_EXCLUSIVE));
        }

        @Override
        public String floorKey(String key) {
            checkNotNullAndContainsNoZeros(key);
            return keyOfStringEntry(delegate.lower(key + KEY_VALUE_SEPARATOR_EXCLUSIVE));
        }

        @Override
        public Entry<String, String> ceilingEntry(String key) {
            checkNotNullAndContainsNoZeros(key);
            return entryOfStringEntry(delegate.ceiling(key + KEY_VALUE_SEPARATOR));
        }

        @Override
        public String ceilingKey(String key) {
            checkNotNullAndContainsNoZeros(key);
            return keyOfStringEntry(delegate.ceiling(key + KEY_VALUE_SEPARATOR));
        }

        @Override
        public Entry<String, String> higherEntry(String key) {
            checkNotNullAndContainsNoZeros(key);
            return entryOfStringEntry(delegate.ceiling(key + KEY_VALUE_SEPARATOR_EXCLUSIVE));
        }

        @Override
        public String higherKey(String key) {
            checkNotNullAndContainsNoZeros(key);
            return keyOfStringEntry(delegate.ceiling(key + KEY_VALUE_SEPARATOR_EXCLUSIVE));
        }

        @Override
        public String firstKey() {
            return keyOfStringEntry(delegate.first());
        }

        @Override
        public String lastKey() {
            return keyOfStringEntry(delegate.last());
        }

        @Override
        public Entry<String, String> firstEntry() {
            return entryOfStringEntry(delegate.first());
        }

        @Override
        public Entry<String, String> lastEntry() {
            return entryOfStringEntry(delegate.last());
        }

        @Override
        public Entry<String, String> pollFirstEntry() {
            return entryOfStringEntry(delegate.pollFirst());
        }

        @Override
        public Entry<String, String> pollLastEntry() {
            return entryOfStringEntry(delegate.pollLast());
        }

        @Override
        public Comparator<? super String> comparator() {
            return delegate.comparator();
        }
        
        @Override
        public Set<Entry<String, String>> entrySet() {
            return new EntrySet(this);
        }

        @Override
        public Collection<String> values() {
            return new Values(this);
        }

        @Override
        public Set<String> keySet() {
            return new KeySet(this, false);
        }

        @Override
        public NavigableSet<String> navigableKeySet() {
            return new KeySet(this, false);
        }

        @Override
        public NavigableSet<String> descendingKeySet() {
            return new KeySet(this, true);
        }

        @Override
        public NavigableMap<String, String> descendingMap() {
            return new SubMap(delegate.descendingSet());
        }

        @Override
        public NavigableMap<String, String> subMap(String fromKey, boolean fromInclusive, String toKey, boolean toInclusive) {
            if (fromKey.compareTo(toKey) > 0)
                throw new IllegalArgumentException("fromKey > toKey");
            if (fromInclusive && fromKey.isEmpty())
                fromKey = null;
            else
                checkNotNullAndContainsNoZeros(fromKey);
            checkNotNullAndContainsNoZeros(toKey);
            return new SubMap(delegate.subSet(fromKey + (fromInclusive ? KEY_VALUE_SEPARATOR : KEY_VALUE_SEPARATOR_EXCLUSIVE), true, toKey + (toInclusive ? KEY_VALUE_SEPARATOR_EXCLUSIVE : KEY_VALUE_SEPARATOR), false));
        }

        @Override
        public NavigableMap<String, String> headMap(String toKey, boolean inclusive) {
            checkNotNullAndContainsNoZeros(toKey);
            return new SubMap(delegate.headSet(toKey + (inclusive ? KEY_VALUE_SEPARATOR_EXCLUSIVE : KEY_VALUE_SEPARATOR), false));
        }

        @Override
        public NavigableMap<String, String> tailMap(String fromKey, boolean inclusive) {
            if (inclusive && fromKey.isEmpty())
                return this;
            checkNotNullAndContainsNoZeros(fromKey);
            return new SubMap(delegate.tailSet(fromKey + (inclusive ? KEY_VALUE_SEPARATOR : KEY_VALUE_SEPARATOR_EXCLUSIVE), true));
        }

        @Override
        public SortedMap<String, String> subMap(String fromKey, String toKey) {
            if (fromKey.compareTo(toKey) > 0)
                throw new IllegalArgumentException("fromKey > toKey");
            if (fromKey.isEmpty())
                fromKey = null;
            else
                checkNotNullAndContainsNoZeros(fromKey);
            checkNotNullAndContainsNoZeros(toKey);
            return new SubMap(delegate.subSet(fromKey + KEY_VALUE_SEPARATOR, true, toKey + KEY_VALUE_SEPARATOR, false));
        }

        @Override
        public SortedMap<String, String> headMap(String toKey) {
            checkNotNullAndContainsNoZeros(toKey);
            return new SubMap(delegate.headSet(toKey + KEY_VALUE_SEPARATOR, false));
        }

        @Override
        public SortedMap<String, String> tailMap(String fromKey) {
            if (fromKey.isEmpty())
                return this;
            checkNotNullAndContainsNoZeros(fromKey);
            return new SubMap(delegate.tailSet(fromKey + KEY_VALUE_SEPARATOR, true));
        }

        private Iterator<String> keyIterator(boolean desc) {
            return DAWGMap.this.keyIterator(delegate, desc);
        }

        private Iterator<Entry<String, String>> entryIterator() {
            return DAWGMap.this.entryIterator(delegate);
        }

        private Iterator<String> valueIterator() {
            return DAWGMap.this.valueIterator(delegate);
        }

        private boolean containsEntry(Entry<String, String> e) {
            checkNotNullAndContainsNoZeros(e.getKey());
            checkNotNullAndContainsNoZeros(e.getValue());
            return delegate.contains(e.getKey() + KEY_VALUE_SEPARATOR + e.getValue());
        }

        @Override
        public boolean remove(Object key, Object value) {
            checkNotNullAndContainsNoZeros(key);
            checkNotNullAndContainsNoZeros(value);
            return delegate.remove((String)key + KEY_VALUE_SEPARATOR + value);
        }

        private boolean removeValue(Object value) {
            checkNotNullAndContainsNoZeros(value);
            Iterator<String> it = ((StringsFilter)delegate).getStringsEndingWith(KEY_VALUE_SEPARATOR + (String)value).iterator();
            if (it.hasNext()) {
                String stringEntry = it.next();
                return dawg.remove(stringEntry);
            }
            return false;
        }

        @Override
        public String remove(Object key) {
            checkNotNullAndContainsNoZeros(key);
            return valueOfStringEntry(pollFirstElement(((StringsFilter)delegate).getStringsStartingWith((String)key + KEY_VALUE_SEPARATOR)), key);
        }

        @Override
        public String put(String key, String value) {
            checkNotNullAndContainsNoZeros(value);
            String old = get(key);
            if (old != null && old.equals(value))
                return old;
            String keyWithSeparator = key + KEY_VALUE_SEPARATOR;
            delegate.add(keyWithSeparator + value);
            if (old != null)
                delegate.remove(keyWithSeparator + old);
            return old;
        }

        @Override
        public String get(Object key) {
            checkNotNullAndContainsNoZeros(key);
            return valueOfStringEntry(getFirstElement(((StringsFilter)delegate).getStringsStartingWith((String)key + KEY_VALUE_SEPARATOR)), key);
        }

        @Override
        public boolean containsKey(Object key) {
            checkNotNullAndContainsNoZeros(key);
            return ((StringsFilter)delegate).getStringsStartingWith((String)key + KEY_VALUE_SEPARATOR).iterator().hasNext();
        }

        @Override
        public boolean containsValue(Object value) {
            checkNotNullAndContainsNoZeros(value);
            return ((StringsFilter)delegate).getStringsEndingWith(KEY_VALUE_SEPARATOR + (String)value).iterator().hasNext();
        }

        @Override
        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        @Override
        public int size() {
            return delegate.size();
        }
    }
}