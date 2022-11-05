select setval('appointment_appointment_id_seq', max(appointment_id))
from appointment;
select setval('doctor_doctor_id_seq', max(doctor_id))
from doctor;
select setval('office_office_id_seq', max(office_id))
from office;
select setval('patient_patient_id_seq', max(patient_id))
from patient;
select setval('picture_picture_id_seq', max(picture_id))
from picture;
select setval('refresh_token_refresh_token_id_seq', max(refresh_token_id))
from refresh_token;
select setval('system_doctor_specialty_specialty_id_seq', max(specialty_id))
from system_doctor_specialty;
select setval('system_locality_locality_id_seq', max(locality_id))
from system_locality;
select setval('system_province_province_id_seq', max(province_id))
from system_province;
select setval('users_users_id_seq', max(users_id))
from users;
select setval('verification_token_verification_token_id_seq', max(verification_token_id))
from verification_token;
select setval('workday_workday_id_seq', max(workday_id))
from workday;
