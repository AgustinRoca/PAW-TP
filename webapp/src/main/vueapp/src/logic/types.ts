import services from './services/services.types';

const TYPES = {
    Repositories: {
        RestRepository: Symbol('RestRepository')
    },
    Services: {
        ...services
    }
};

export default TYPES;
