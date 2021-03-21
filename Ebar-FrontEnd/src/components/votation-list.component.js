import React, {useState} from 'react';
import events from '../img/even.jpg'
import {List, ListItem, ListItemText, Collapse, Button} from '@material-ui/core';
import {ExpandLess, ExpandMore} from '@material-ui/icons';
import styles from '../styles/votations.css'
import { makeStyles } from '@material-ui/core/styles';

function Votations(){

    const vot = [{id:1, titulo: 'Canción siguiente', descripcion:'Temazo', opciones: [{1:'Despacito'}, {2:'Baby Shark'}, {3: 'Dale Don Dale'}]},
        {id:2, titulo: 'Canción segunda', descripcion: 'Temón', opciones: [{ 1: 'Despacito' }, { 2: 'Baby Shark' }, { 3: 'Dale Don Dale' }] }]
    const [expanded, setExpanded] = useState({});

    const handleClick = (id) => {
        setExpanded({
            ...expanded,
            [id]: !expanded[id]
        });
    }
        


    return (
        <div>
            <div className='container'>
                <img className='img' alt="events" src={events}/>
                <div className="div-vot">Votaciones</div>
            </div>
            <div>
                <h5 style={styles.h5}>
                    A continuación, podrá encontrar la lista de votaciones disponibles en las que puede participar
                </h5>
            </div>
            <div style={{marginTop:'40px'}}>
                <List component="nav">
                    {vot.map(x =>
                    <div>
                        <ListItem button onClick={() => handleClick(x.id)} style={{...stylesComponent.listitem }}>
                            <ListItemText primary={x.titulo}/>
                            <Button variant="contained" size='small' target="_blank" href="http://www.google.es" color="primary" 
                            style={{...stylesComponent.button}} >
                                Acceder
                            </Button>
                            {!expanded[x.id] ? <ExpandLess /> : <ExpandMore />}
                        </ListItem>
                        <Collapse in={expanded[x.id]} timeout="auto" unmountOnExit>
                        <List component="div" disablePadding style={{ ...stylesComponent.listdetail }}>
                            <ListItem >
                                <ListItemText primary={x.descripcion} />
                            </ListItem>
                        </List>
                        </Collapse>
                    </div>
                    )}
                </List>
            </div>
        </div>
    )
}

const stylesComponent = {
    button: {
        backgroundColor: '#007bff',
        marginRight: '60px',
        textTransform: 'none',
        letterSpacing: 'normal',
        fontSize: '15px',
        fontWeight: '600'
    },
    listdetail: {
        backgroundColor: 'rgba(215, 211, 211, 0.3)',
        borderRadius: '5px',
        height: '55px',
        marginTop: '3px'
    },
    listitem: {
        backgroundColor: 'rgba(215, 211, 211, 0.74)',
        borderRadius: '5px',
        height: '55px',
        marginTop: '3px'
    }
}

export default Votations