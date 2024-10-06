import axios, { AxiosInstance } from 'axios';

interface nextPhase {
  cards: number[];
  winner: number;
}
interface nextMove {
  move: string;
  actionPlayer: number;
  raiseAmount: number;
}

class gameApi {
  private axiosInstance: AxiosInstance;

  constructor(baseURL: string) {
    this.axiosInstance = axios.create({
      baseURL,
    });
  }

//   public async getUserProfile(userId: string) { // this is for testing purposes
//     try {
//       const response = await this.axiosInstance.get(`/users/${userId}`);
//       return response.data;
//     } catch (error) {
//       throw error;
//     }
//   }

  public async pollMove(userId: number, gameId: number) {
    try{
      const response = await this.axiosInstance.get<nextMove>('/poll-moves', {
        params: {
          userId: userId,
          gameId: gameId,
        },
      });
      return response.data;
    } catch (error) {
      console.error(error);
    }
  }

  public async pollNextPhase(userId: number, gameId: number){
    try{
      const response = await this.axiosInstance.get<nextPhase>('/poll-nextphase', {
        params: {
          userId: userId,
          gameId: gameId,
        },
      });
      return response.data;
    } catch (error) {
      console.error(error);
    }
  }
}

// note that this is probably not the actual address
const api = new gameApi('http://localhost:3001/api/v1/game');
export default api;