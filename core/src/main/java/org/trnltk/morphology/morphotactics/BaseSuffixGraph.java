package org.trnltk.morphology.morphotactics;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class BaseSuffixGraph implements SuffixGraph {
    private Map<String, SuffixGraphState> stateMap;
    private Map<String, Suffix> suffixMap;

    private SuffixGraph decorated;


    protected BaseSuffixGraph() {
        this(new EmptySuffixGraph());
    }

    protected BaseSuffixGraph(SuffixGraph decorated) {
        this.stateMap = new HashMap<String, SuffixGraphState>();
        this.suffixMap = new HashMap<String, Suffix>();
        this.decorated = decorated;
    }

    @Override
    public final void initialize() {
        this.decorated.initialize();

        this.registerEverything();
        this.stateMap = ImmutableMap.copyOf(stateMap);
        this.suffixMap = ImmutableMap.copyOf(suffixMap);
        this.validate();
    }

    @Override
    public SuffixGraphState getDefaultStateForRoot(Root root) {
        //first look at self
        Validate.isTrue(this.stateMap instanceof ImmutableMap, "Suffix graph not initialized yet!");
        Validate.isTrue(this.suffixMap instanceof ImmutableMap, "Suffix graph not initialized yet!");
        final SuffixGraphState suffixGraphState = this.doGetDefaultStateForRoot(root);
        if (suffixGraphState == null)
            //then look at the decorated
            return this.decorated.getDefaultStateForRoot(root);
        else
            return suffixGraphState;
    }

    protected SuffixGraphState registerState(String name, SuffixGraphStateType suffixGraphStateType, SyntacticCategory syntacticCategory) {
        final SuffixGraphState suffixGraphState = new SuffixGraphState(name, suffixGraphStateType, syntacticCategory);
        Validate.isTrue(!this.stateMap.containsKey(name));
        Validate.isTrue(!this.stateMap.containsValue(suffixGraphState));
        Validate.isTrue(this.decorated.getSuffixGraphState(name) == null);
        this.stateMap.put(name, suffixGraphState);
        return suffixGraphState;
    }

    protected Suffix registerSuffix(String name) {
        return registerSuffix(name, name);
    }

    protected Suffix registerSuffix(String name, String prettyName) {
        return registerSuffix(name, null, prettyName);
    }

    protected Suffix registerSuffix(String name, SuffixGroup group) {
        return registerSuffix(name, group, name);
    }

    protected Suffix registerSuffix(String name, SuffixGroup group, String prettyName) {
        return registerSuffix(name, group, prettyName, false);
    }

    protected Suffix registerSuffix(String name, SuffixGroup group, String prettyName, boolean allowRepetition) {
        final Suffix suffix = new Suffix(name, group, prettyName, allowRepetition);
        putSuffixToMap(name, suffix);
        return suffix;
    }

    protected FreeTransitionSuffix registerFreeTransitionSuffix(String name) {
        final FreeTransitionSuffix freeTransitionSuffix = new FreeTransitionSuffix(name);
        putSuffixToMap(name, freeTransitionSuffix);
        return freeTransitionSuffix;
    }

    protected ZeroTransitionSuffix registerZeroTransitionSuffix(String name) {
        final ZeroTransitionSuffix zeroTransitionSuffix = new ZeroTransitionSuffix(name);
        putSuffixToMap(name, zeroTransitionSuffix);
        return zeroTransitionSuffix;
    }

    private void putSuffixToMap(String name, Suffix suffix) {
        Validate.isTrue(!this.suffixMap.containsKey(name));
        Validate.isTrue(!this.suffixMap.containsValue(suffix));
        Validate.isTrue(this.decorated.getSuffix(name) == null);
        this.suffixMap.put(name, suffix);
    }

    @Override
    public Suffix getSuffix(String name) {
        final Suffix suffix = this.suffixMap.get(name);
        if (suffix == null)
            return this.decorated.getSuffix(name);
        else
            return suffix;
    }

    @Override
    public SuffixForm getSuffixForm(String suffixName, String suffixFormStr) {
        final Suffix suffix = this.getSuffix(suffixName);
        for (SuffixForm suffixForm : suffix.getSuffixForms()) {
            if (suffixForm.getForm().getSuffixFormStr().equals(suffixFormStr))
                return suffixForm;
        }

        return null;
    }

    @Override
    public SuffixGraphState getSuffixGraphState(String stateName) {
        final SuffixGraphState suffixGraphState = this.stateMap.get(stateName);
        if (suffixGraphState == null)
            return this.decorated.getSuffixGraphState(stateName);
        else
            return suffixGraphState;
    }

    @Override
    public LinkedList<Suffix> getAllSuffixes() {
        Validate.isTrue(this.suffixMap instanceof ImmutableMap, "Suffix graph not initialized yet!");
        return Lists.newLinkedList(Iterables.concat(this.suffixMap.values(), this.decorated.getAllSuffixes()));
    }

    protected void validate() {
        for (String stateName : this.stateMap.keySet()) {
            Validate.isTrue(this.decorated.getSuffixGraphState(stateName) == null, "State " + stateName + " also exists in decorated graph!");
        }
        for (String suffixName : this.suffixMap.keySet()) {
            Validate.isTrue(this.decorated.getSuffix(suffixName) == null, "Suffix " + suffixName + " also exists in decorated graph!");
        }
    }

    protected abstract void registerEverything();

    protected abstract SuffixGraphState doGetDefaultStateForRoot(Root root);
}
