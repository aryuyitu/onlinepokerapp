import axios, { AxiosInstance } from 'axios';

interface playerInfo {
  playerId: number,
  username: string,
  chipReserve: number,
}
// instead of an interface i can have codes in the negatives to return what went wrong
// -1 : username or password is wrong
// -2 : try again, something happened
// -3 : username is taken
// -4 : email is taken

class playerApi {
  private axiosInstance: AxiosInstance;

  constructor(baseURL: string) {
    this.axiosInstance = axios.create({
      baseURL,
    });
  }

  public async login(username: string, password: string) {
    try {
      const response = await this.axiosInstance.get<playerInfo>('/login', {
        params: { username, password },
        headers: {'Accept': 'application/json'},
      });
      console.log(response.data);
      if(response.data.playerId > 0){
        sessionStorage.setItem('playerInfo',JSON.stringify(response.data));
      }
      return response.data.playerId;
    } catch (error) {
      console.log(error);
      return -2;
    }
  }

  public async register(username: string, password: string, email: string) {
    try{
      const response = await this.axiosInstance.post<playerInfo>('/register', null, { 
        params: { email, username, password},
        headers: {'Accept': 'application/json'},
      });
      if(response.data.playerId > 0){
        sessionStorage.setItem('playerInfo',JSON.stringify(response.data));
      }
      return response.data.playerId;
    }catch(error){
      console.log(error);
      return -2
    }
  }

  public async getUserInfo(userId: number) { // this is for testing purposes
    try {
      const response = await this.axiosInstance.get(`/users/${userId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  }


}

// note that this is probably not the actual address
const api = new playerApi('http://localhost:8080/api/v1/player');
export default api;