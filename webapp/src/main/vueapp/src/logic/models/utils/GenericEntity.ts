import {ID} from '~/logic/Utils';

interface GenericEntityID {
    id: ID;
}

export abstract class GenericEntity<T = {}> implements GenericEntityID {
    abstract id: ID;
}
