import {ActionContext, Store} from 'vuex';

export type DefineMutationTree<Mutation, State> = {
    [Prop in keyof Mutation]: (state: State, handler: { payload: Mutation[Prop] }) => void;
};

export type DefineTypes<Methods> = {
    [Prop in keyof Methods]: Methods[Prop] extends undefined
        ? () => { type: Prop }
        : (payload: Methods[Prop]) => { type: Prop; payload: Methods[Prop] };
};

type SameKeys<Action> = {
    [Prop in keyof Action]?: any
} | undefined;

export type DefineActionTree<Action, State, RootState, ReturnType extends SameKeys<Action> = undefined> = {
    [Prop in keyof Action]: Action[Prop] extends undefined ?
        (
            this: Store<RootState>,
            ctx: ActionContext<State, RootState>,
        ) => ReturnType extends undefined ?
            (void | Promise<any>)
            :
            // @ts-ignore
            ReturnType[Prop] extends undefined ?
                (void | Promise<any>)
                :
                // @ts-ignore
                ReturnType[Prop] | Promise<ReturnType[Prop]>
        :
        (
            this: Store<RootState>,
            ctx: ActionContext<State, RootState>,
            handler: { payload: Action[Prop] },
            // @ts-ignore
        ) => ReturnType extends undefined ?
            (void | Promise<any>)
            :
            // @ts-ignore
            ReturnType[Prop] extends undefined ?
                (void | Promise<any>)
                :
                // @ts-ignore
                ReturnType[Prop] | Promise<ReturnType[Prop]>;
};

export type DefineGetterTree<Getter, State, RootState = {}, RootGetter = {}> = {
    [K in keyof Getter]: (
        state: State,
        getters: Getter,
        rootState: RootState,
        rootGetters: RootGetter,
    ) => Getter[K];
};

export type GetterHelper<Getter> = { [Prop in keyof Getter]: Getter[Prop] };

export type StoreTS<State, Getters> = Omit<Store<State>, 'getters'> & {
    readonly getters: GetterHelper<Getters>;
};

export interface AsyncProperty<T> {
    promise: Promise<T> | null
}

export interface CacheableAsyncProperty<T> extends AsyncProperty<T> {
    loaded?: boolean
}
