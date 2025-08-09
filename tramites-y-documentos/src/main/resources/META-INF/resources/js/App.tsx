import React, { useEffect, useState } from "react";
import liferayAxios from "../interceptor/axiosLiferay";
import { UserService } from "../service/UserService";
const App: React.FC = () => {
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

  const [result, setResult] = useState();
  const userService = new UserService();
  useEffect(() => {
    setResult(loadResult);
  }, []);

  const loadResult: any = async () => {
    const data = await userService.getUserAuth();
    console.log("USER AUTH: ", data);
    return data;
  };

  return (
    <div>
      {/* <button onClick={() => printToken()}>Print Token</button> */}
      CONSULTA DE LA API "ME" DEL KORE
      {result && <div>{result}</div>}
    </div>
  );
};

export default App;
