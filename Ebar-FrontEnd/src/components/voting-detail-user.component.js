import React, { Component } from "react";
import {FormHelperText, FormControl, Radio, RadioGroup, FormControlLabel, Button, Divider, CardContent, Typography, Card} from "@material-ui/core"
import Icon from '@material-ui/core/Icon';

export default class VotingDetailUser extends Component{
    constructor(props) {
      super(props)
      this.state = {
         currentBar: {
             id: null,
             nombre:"",
             descripcion: "",
             ubicacion: ""
         },
         value: " ",
         error: false,
         helperText: "Elige bien",
         voting: {
          id: 1,
          titulo: "Votación para música",
          descripcion: "Una votación para elegir la música",
          inicio: "20-03-2021 21:24:00",
          fin: null,
          opciones: [
            {
              id: 1,
              nombre: "Despacito",
              votacion: 1
            },
            {
              id: 2,
              nombre: "Despacito otra vez",
              votacion: 1
            }
  
          ]
        }
      };
    }

    handleSubmit = (event) => {
      
    }

    handleRadioChange = (event) => {
      event.persist()

      console.log(event.target.value)

      this.setState({
        value: event.target.value,
        helperText: " ",
        error: false
      })
    }

    createOptions = (options) => {
      let res = [];
      
      for (let i = 0; i < options.length; i++) {
        res.push(<FormControlLabel key={options[i].id.toString()} value={options[i].id.toString()} control={<Radio />} label={options[i].nombre}/>)
      }

      return res;
    }

    render(){
      const {voting ,value, error, helperText} = this.state;

      return (
        <div>
          <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons" />
          <div>
            <Card className=".card">
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  Votación
                </Typography>
                <Typography variant="h5" component = "h2">
                  {voting.titulo}
                </Typography>
                <br/>
                <Typography variant="body2" component="p">
                  {voting.descripcion}
                </Typography>
                <br/>
                <Typography color="textSecondary" variant="subtitle2" gutterBottom>
                  {voting.inicio}
                </Typography>
              </CardContent>
            </Card>
          </div>
          <br/>
          <Divider />
          <br/>
          <div id="voting_options_id" name="voting_options">
            <Card>
              <CardContent>
                <form onSubmit={this.handleSubmit}>
                  <FormControl component="fieldset" error={error} className=".formControl">
                    <RadioGroup aria-label="options" name="options" value={value} onChange={this.handleRadioChange}>
                      {this.createOptions(voting.opciones)}  
                    </RadioGroup>
                    <FormHelperText>{helperText}</FormHelperText>
                  <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    className="button"
                    endIcon={<Icon>send</Icon>}>
                    Enviar votación
                  </Button>
                  </FormControl>
                </form>
              </CardContent>
            </Card>
          </div>
        </div>
      );
    }
    
    
}