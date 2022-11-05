import {APIResponse} from '~/logic/models/APIResponse';
import {Hash} from '~/logic/Utils';

export type PostConfig<T, P extends Hash<string> | undefined = {}> = {
    params?: P;
    data: T;
    contentType: string;
    accepts: string | string[];
    paginate?: boolean;
    retry?: boolean
}

export type PutConfig<T> = PostConfig<T>;

export type GetConfig<T> = Partial<Omit<PostConfig<T>, 'accepts'>> & Pick<PostConfig<T>, 'accepts'>;

export type DeleteConfig<T> = Partial<PostConfig<T>>;

export const StatusCodes = {
    BAD_REQUEST: 400,
    UNAUTHORIZED: 401,
    FORBIDDEN: 403,
    NOT_FOUND: 404,
    UNPROCESSABLE_ENTITY: 422
};

export const LOGGED_IN_TTL_HEADER_NAME = 'x-logged-in-ttl';
export const REFRESH_TOKEN_TTL_HEADER_NAME = 'x-refresh-token-ttl';

export interface RestRepository {
    get<R, T = any>(path: string, config: GetConfig<T>): Promise<APIResponse<R>>;

    post<R, T>(path: string, config: PostConfig<T>, ignoreErrors?: boolean): Promise<APIResponse<R>>;

    put<R, T>(path: string, config: PutConfig<T>): Promise<APIResponse<R>>;

    delete<R = any, T = any>(path: string, config?: DeleteConfig<T>): Promise<APIResponse<R>>;
}
