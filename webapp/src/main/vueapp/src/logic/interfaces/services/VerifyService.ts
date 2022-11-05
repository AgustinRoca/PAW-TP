import {APIError} from '~/logic/models/APIError';

export interface VerifyService {
    verify(token: string): Promise<true | APIError>;
}
