import {
  Create,
  Datagrid,
  Edit,
  List,
  SelectField,
  SelectInput,
  Show,
  SimpleForm,
  SimpleShowLayout,
  TextField,
  TextInput,
  required,
} from "react-admin";

const stateChoices = [
  { id: "DRAFT", name: "Draft" },
  { id: "PUBLISHED", name: "Published" },
];

const BulletinForm = () => (
  <SimpleForm>
    <TextInput source="title" fullWidth validate={[required()]} />
    <TextInput
      source="description"
      multiline
      minRows={3}
      fullWidth
      validate={[required()]}
    />
    <SelectInput
      source="state"
      choices={stateChoices}
      validate={[required()]}
    />
    <TextInput source="contact" fullWidth validate={[required()]} />
  </SimpleForm>
);

export const BulletinList = () => (
  <List>
    <Datagrid rowClick="show">
      <TextField source="id" />
      <TextField source="title" />
      <SelectField source="state" choices={stateChoices} />
      <TextField source="contact" />
    </Datagrid>
  </List>
);

export const BulletinShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="id" />
      <TextField source="title" />
      <TextField source="description" />
      <SelectField source="state" choices={stateChoices} />
      <TextField source="contact" />
    </SimpleShowLayout>
  </Show>
);

export const BulletinEdit = () => <Edit children={<BulletinForm />} />;

export const BulletinCreate = () => <Create children={<BulletinForm />} />;
