import React, { Component } from 'react';
import MesaDataService from '../services/mesa.service';

export default class MesaDetails extends Component {
  constructor(props) {
    super(props)
    this.getMesasDetails(id) = this.getMesasDetails(id).bind(this);
    this.state = {
       mesaActual : {
           id : null,
           nombre: "",
           token: "", 
           estadoMesa: "",
           bar_id: null,
           trabajador_id: null
       }
    };
  };
  
  componentDidMount() {
      this.getMesasDetails(1);
  }


  getMesasDetails(id) {
      MesaDataService.getMesaDetails(id).then(res => { 
          this.setState({
              mesaActual : res.data
          })
          console.log(res.data);
      })
      .catch(e => {
          console.log(e);
      })
  }
  
    render() {
        const {mesaActual} = this.state
    return (
        <div>
            <p>Este es el componente de los detalles de la mesa.</p>  
            <p>{mesaActual.nombre}</p>
            <p>{mesaActual.token}</p>
            <p>{mesaActual.estadoMesa}</p>
        </div>
    );
  }
}
