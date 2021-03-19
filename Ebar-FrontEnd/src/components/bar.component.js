import React, { Component } from "react";
import BarDataService from "../services/bar.service";

export default class Bar extends Component{
    constructor(props) {
      super(props)
      this.getBar = this.getBar.bind(this);  
      this.state = {
         currentBar: {
             id: null,
             nombre:"",
             descripcion: "",
             ubicacion: ""
         },
        message: ""
      };
    };

    componentDidMount(){
        this.getBar(this.props.match.params.id);
    }

    
    
}