import React from 'react';
//import logo from './logo.svg';
import './App.css';

import GamePage from './pages/GamePage';
import { BrowserRouter as Router, Route, Switch, Redirect } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import GameSelectorPage from './pages/GameSelectorPage';
import RegisterPage from './pages/RegisterPage';

function App() {
  return (
    <Router>
      <Switch>
        <Route exact path="/game">
          <GamePage />
        </Route>
        <Route path="/login">
          <LoginPage />
        </Route>
        <Route path="/register">
          <RegisterPage />
        </Route>
        <Route path="/game-selector">
          <GameSelectorPage />
        </Route>
        <Redirect to="/login" /> {/* Default route */}
      </Switch>
    </Router>
  );
}

export default App;