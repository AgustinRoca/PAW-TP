import {Container} from 'inversify';
import 'reflect-metadata';
import {RestRepository} from '~/logic/interfaces/repositories/RestRepository';
import TYPES from '~/logic/types';
import {RestRepositoryImpl} from '~/logic/repositories/RestRepositoryImpl';
import {CountryService} from '~/logic/interfaces/services/CountryService';
import {CountryServiceImpl} from '~/logic/services/CountryServiceImpl';
import {AppointmentService} from '~/logic/interfaces/services/AppointmentService';
import {AppointmentServiceImpl} from '~/logic/services/AppointmentServiceImpl';
import {AppointmentTimeSlotServiceImpl} from '~/logic/services/AppointmentTimeSlotServiceImpl';
import {LocalityServiceImpl} from '~/logic/services/LocalityServiceImpl';
import {LocalityService} from '~/logic/interfaces/services/LocalityService';
import {AppointmentTimeSlotService} from '~/logic/interfaces/services/AppointmentTimeSlotService';
import {ProvinceService} from '~/logic/interfaces/services/ProvinceService';
import {ProvinceServiceImpl} from '~/logic/services/ProvinceServiceImpl';
import {DoctorServiceImpl} from '~/logic/services/DoctorServiceImpl';
import {DoctorService} from '~/logic/interfaces/services/DoctorService';
import {DoctorSpecialtyService} from '~/logic/interfaces/services/DoctorSpecialtyService';
import {UserService} from '~/logic/interfaces/services/UserService';
import {WorkdayService} from '~/logic/interfaces/services/WorkdayService';
import {WorkdayServiceImpl} from '~/logic/services/WorkdayServiceImpl';
import {UserServiceImpl} from '~/logic/services/UserServiceImpl';
import {DoctorSpecialtyServiceImpl} from '~/logic/services/DoctorSpecialtyServiceImpl';
import {AuthService} from '~/logic/interfaces/services/AuthService';
import {AuthServiceImpl} from '~/logic/services/AuthServiceImpl';
import {VerifyService} from '~/logic/interfaces/services/VerifyService';
import {VerifyServiceImpl} from '~/logic/services/VerifyServiceImpl';

const container = new Container();

container.bind<RestRepository>(TYPES.Repositories.RestRepository).to(RestRepositoryImpl).inSingletonScope();

container.bind<AppointmentService>(TYPES.Services.AppointmentService).to(AppointmentServiceImpl).inSingletonScope();
container.bind<AppointmentTimeSlotService>(TYPES.Services.AppointmentTimeSlotService).to(AppointmentTimeSlotServiceImpl).inSingletonScope();
container.bind<AuthService>(TYPES.Services.AuthService).to(AuthServiceImpl).inSingletonScope();
container.bind<CountryService>(TYPES.Services.CountryService).to(CountryServiceImpl).inSingletonScope();
container.bind<LocalityService>(TYPES.Services.LocalityService).to(LocalityServiceImpl).inSingletonScope();
container.bind<ProvinceService>(TYPES.Services.ProvinceService).to(ProvinceServiceImpl).inSingletonScope();
container.bind<DoctorService>(TYPES.Services.DoctorService).to(DoctorServiceImpl).inSingletonScope();
container.bind<DoctorSpecialtyService>(TYPES.Services.DoctorSpecialtyService).to(DoctorSpecialtyServiceImpl).inSingletonScope();
container.bind<UserService>(TYPES.Services.UserService).to(UserServiceImpl).inSingletonScope();
container.bind<VerifyService>(TYPES.Services.VerifyService).to(VerifyServiceImpl).inSingletonScope();
container.bind<WorkdayService>(TYPES.Services.WorkdayService).to(WorkdayServiceImpl).inSingletonScope();

export default container;
