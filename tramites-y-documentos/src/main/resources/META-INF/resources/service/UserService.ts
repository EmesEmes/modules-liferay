import liferayAxios from "../interceptor/axiosLiferay";

export class UserService {
  url: string;
  constructor() {
    this.url = "/user";
  }

  // const printToken = () => {
  //   console.log("Entro al printToken")
  //   liferayAxios.get('/token/getToken').then((res) => {
  //       console.log(res);
  //   })
  //   .catch((err) => {
  //       console.log("Entro al printToken Error")
  //       setError(err.message);
  //   });
  // };

  async getUserAuth() {
    try {
      const res = await liferayAxios.get(`${this.url}/me`);
      return res.data;
    } catch (error) {
      throw error;
    }
  }
}
