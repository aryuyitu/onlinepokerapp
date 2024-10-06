import React, { useState } from 'react';
import playerApi from '../api/playerApi';
import "./loginPage.css"
import { ErrorCodes } from '../errors/errorCodes';
import { useHistory } from 'react-router-dom';

const RegisterPage: React.FC = () => {
  const [username, setUsername] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [error, setError] = useState<string>('');
  const [playerID, setPlayerID] = useState<number>(0);

  const history = useHistory();

  const handleUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUsername(e.target.value);
  };

  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setEmail(e.target.value);
  };

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (username === '' || password === '' || email === '') {
      setError('All fields are required');
      return;
    }
    setError('');

    try {
      await playerApi.register(username,password,email).then((data) => {
        console.log(data);
        setPlayerID(data);
        if(data > 0){
          sessionStorage.setItem('PLAYERID', data.toString());
          history.push("./game-selector");
        }
      });
    } catch (err) {
      setPlayerID(-2);
    }
    console.log('Username:', username);
    console.log('Password:', password);
  };

  return (
    <div className="main">
    <div className="main-container">
      <h3>Register here</h3>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={handleUsernameChange}
          />
        </div>
        <div>
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={handleEmailChange}
          />
        </div>
        <div className="input-section">
          <label htmlFor="password">Password:</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={handlePasswordChange}
          />
        </div>
        <button className="submit-button" type="submit">Login</button>
        {playerID < 0 && (
          <div>
            {ErrorCodes.get(playerID)}
          </div>
        )}
        {error && <p className="error">{error}</p>}
        <a href="./login">Have an account? Login!</a>
      </form>
    </div>
    </div>
  );
};

export default RegisterPage;