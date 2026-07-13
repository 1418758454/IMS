export const EDUCATION_LEVELS = {
  UNDERGRAD: '本科',
  MASTER: '硕士',
  DOCTOR: '博士'
};

export function normalizeEducationLevel(level) {
  if (!level) return '';
  if (level.includes(EDUCATION_LEVELS.DOCTOR)) return EDUCATION_LEVELS.DOCTOR;
  if (level.includes(EDUCATION_LEVELS.MASTER)) return EDUCATION_LEVELS.MASTER;
  if (level.includes(EDUCATION_LEVELS.UNDERGRAD)) return EDUCATION_LEVELS.UNDERGRAD;
  return level;
}

export function hasMasterEducation(level) {
  const normalized = normalizeEducationLevel(level);
  return normalized === EDUCATION_LEVELS.MASTER || normalized === EDUCATION_LEVELS.DOCTOR;
}

export function hasDoctorEducation(level) {
  return normalizeEducationLevel(level) === EDUCATION_LEVELS.DOCTOR;
}

export function getCurrentHighestEducationTime(data) {
  const normalized = normalizeEducationLevel(data.highestEducation);
  if (normalized === EDUCATION_LEVELS.DOCTOR) {
    return data.doctorTime || data.highestEducationTime || '';
  }
  if (normalized === EDUCATION_LEVELS.MASTER) {
    return data.masterTime || data.highestEducationTime || '';
  }
  if (normalized === EDUCATION_LEVELS.UNDERGRAD) {
    return data.undergradTime || data.highestEducationTime || '';
  }
  return data.highestEducationTime || '';
}

export function normalizeEducationPayload(data) {
  const payload = { ...data };
  const normalized = normalizeEducationLevel(payload.highestEducation);

  if (normalized === EDUCATION_LEVELS.UNDERGRAD) {
    payload.masterSchool = '';
    payload.masterTime = null;
    payload.doctorSchool = '';
    payload.doctorTime = null;
    payload.postdoctoralExperience = '';
  } else if (normalized === EDUCATION_LEVELS.MASTER) {
    payload.doctorSchool = '';
    payload.doctorTime = null;
    payload.postdoctoralExperience = '';
  }

  payload.highestEducationTime = getCurrentHighestEducationTime(payload) || null;
  return payload;
}
