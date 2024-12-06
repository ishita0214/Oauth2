export class User {
    id: number;
    name: string;
    email: string;
    image: string;
    accessToken: string;
   

    constructor(id: number, name: string, email: string, image: string, accessToken: string) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.image = image;
        this.accessToken = accessToken;
        
    }
}
