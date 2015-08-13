package io.sphere.sdk.states.commands;

import io.sphere.sdk.commands.CreateCommand;
import io.sphere.sdk.expansion.MetaModelExpansionDsl;
import io.sphere.sdk.states.State;
import io.sphere.sdk.states.StateDraft;
import io.sphere.sdk.states.expansion.StateExpansionModel;

/** Creates a state.

 <p>Example:</p>
 {@include.example io.sphere.sdk.states.commands.StateCreateCommandTest#execution()}

 @see io.sphere.sdk.states.StateDraftBuilder
 */
public interface StateCreateCommand extends CreateCommand<State>, MetaModelExpansionDsl<State, StateCreateCommand, StateExpansionModel<State>> {
    static StateCreateCommand of(final StateDraft draft) {
        return new StateCreateCommandImpl(draft);
    }
}
