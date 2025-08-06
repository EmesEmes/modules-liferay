import axios from 'axios';

const liferayAxios = axios.create({
  baseURL: '/o/liferayService',
  withCredentials: true,
  headers: {
    'Accept': 'application/json',
    'Content-Type': 'application/json',
  },
});

liferayAxios.interceptors.request.use((config) => {
  const token = Liferay.authToken;

  if (token) {
    config.headers['x-csrf-token'] = token;
  }

  return config;
});

export default liferayAxios;