import React, { Component } from "react";
import { Switch, Route, Link } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";
import logo from "./img/ebarIcon.png"
import BarList from "./components/bar-list.component";
import Votations from "./components/votation-list.component";

class App extends Component {


  render() {
    const style = {
      height: '40px',
    }
    return (
      <div>
        <nav className="navbar navbar-expand navbar-light bg-primary">
          <Link to={"/bares"} className="navbar-brand">
            <img src={logo} alt="Logo E-Bar" style={style}></img>
          </Link>
          <div className="navbar-nav">
            <li className="nav-item active">
              <Link to={"/bares"} className="nav-link">
                Bares
              </Link>
            </li>
            <li className="nav-item active">
              <Link to="/votations" className="nav-link">
                Votations
              </Link>
            </li>
          </div>
        </nav>

        <div className="container mt-3">
          <Switch>
            <Route exact path={"/bares"} component={BarList}/>
            <Route exact path={"/votations"} component={Votations} />
          </Switch>
        </div>
      </div>
    );
  }
}

export default App;
