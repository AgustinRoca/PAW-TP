import {inject, injectable} from 'inversify';
import TYPES from '~/logic/types';
import {RestRepository} from '~/logic/interfaces/repositories/RestRepository';
import {getPathWithId, JSON_MIME} from '~/logic/services/Utils';
import {APIError, ErrorMIME} from '~/logic/models/APIError';
import {VerifyService} from '~/logic/interfaces/services/VerifyService';

@injectable()
export class VerifyServiceImpl implements VerifyService {
    private static PATH = 'verify';

    @inject(TYPES.Repositories.RestRepository)
    private rest: RestRepository;

    public async verify(token: string): Promise<true | APIError> {
        let response = await this.rest.post<undefined, undefined>(getPathWithId(VerifyServiceImpl.PATH, token), {
            accepts: ErrorMIME,
            data: undefined,
            contentType: JSON_MIME
        });

        return response.isOk ? true : response.error!;
    }
}
