import http from "../http-common";

export default class MesaDataService{

    getMesaDetails(id) {
        return http.get(`/detallesMesa/${id}`);
    }
}