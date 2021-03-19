import React, { Component } from "react";
import BarDataService from "../services/bar.service";

export default class BarList extends Component {
  
  constructor(props) {
    super(props)
    this.getAllBares = this.getAllBares.bind(this);
    console.log('El componente aun no está disponible en el DOM');
    this.state = {
        bares: [],
        currentIndex: -1, 
    };
  };

  componentDidMount() {
      this.getAllBares();
      console.log('El componente está disponible en el DOM');
  }
  
  getAllBares() {
      BarDataService.getAll().then(res => {
          this.setState({
              bares : res.data
          });
          console.log(res.data);
      })
      .catch(e => {
          console.log("El error es ",e);
      });
  }

  refreshList() {
    this.getAllBares();
    this.setState({
      currentBar: null,
      currentIndex: -1
    });
  }
  
  
    render() {
        const {bares} = this.state;
    return (
      <div className="list row"> 
        <div className="col-md-6">
            <h3 className="align-content-center">Lista de Bares</h3>
            <table className="table" >
             
                <thead>
                    <tr>
                        <th scope="col">Nombre</th>
                        <th scope="col">Descripcion</th>
                        <th scope="col">Ubicacion</th>
                    </tr>
                </thead>
                <tbody>
                {bares  && bares.map((bar, idx) => (
                    <tr>
                        <td>{bar.nombre}</td>
                        <td>{bar.descripcion}</td>
                        <td>{bar.ubicacion}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
        <div>
              <br />
              <p>Esto es un pequeño ejemplo para asegurar el correcto funcionamiento</p>
        </div>
      </div>
    );
  }
}
