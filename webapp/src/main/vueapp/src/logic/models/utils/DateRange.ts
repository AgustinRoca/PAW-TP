export interface FullDate {
    year: number;
    month: number;
    day: number;
}

export interface DateRange {
    from: FullDate;
    to: FullDate;
}

export interface WorkdayTime {
    hour: number,
    minute: number
}

export enum Day {
    MONDAY = 'MONDAY',
    TUESDAY = 'TUESDAY',
    WEDNESDAY = 'WEDNESDAY',
    THURSDAY = 'THURSDAY',
    FRIDAY = 'FRIDAY',
    SATURDAY = 'SATURDAY',
    SUNDAY = 'SUNDAY'
}