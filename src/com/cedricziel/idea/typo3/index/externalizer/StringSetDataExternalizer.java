package com.cedricziel.idea.typo3.index.externalizer;

import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import gnu.trove.THashSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class StringSetDataExternalizer implements DataExternalizer<Set<String>> {
    public StringSetDataExternalizer() {
    }

    public synchronized void save(@NotNull DataOutput out, Set<String> value) throws IOException {
        out.writeInt(value.size());
        Iterator var3 = value.iterator();

        while(var3.hasNext()) {
            String s = (String)var3.next();
            EnumeratorStringDescriptor.INSTANCE.save(out, s);
        }

    }

    public synchronized Set<String> read(@NotNull DataInput in) throws IOException {
        THashSet set = new THashSet();

        for(int r = in.readInt(); r > 0; --r) {
            set.add(EnumeratorStringDescriptor.INSTANCE.read(in));
        }

        return set;
    }
}
