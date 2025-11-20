import { Admin, Resource } from "react-admin";
import { Layout } from "./Layout";
import { dataProvider } from "./dataProvider";
import {
  BulletinList,
  BulletinCreate,
  BulletinEdit,
  BulletinShow,
} from "./bulletins";

export const App = () => (
  <Admin layout={Layout} dataProvider={dataProvider}>
    <Resource
      name="bulletins"
      list={BulletinList}
      create={BulletinCreate}
      edit={BulletinEdit}
      show={BulletinShow}
      recordRepresentation="title"
    />
  </Admin>
);
