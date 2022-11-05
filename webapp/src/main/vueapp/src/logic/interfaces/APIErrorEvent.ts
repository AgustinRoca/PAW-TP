import {APIError} from '~/logic/models/APIError';

export const APIErrorEventName = "APIError";

export type APIErrorCallback = (error: number) => {};
