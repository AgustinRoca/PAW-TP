import {Container} from 'inversify';

declare module 'vue/types/vue' {
    interface Vue {
        $container: Container
    }
}