import {AppRegistry} from 'react-native';
import App from './src/components/home';
import {name as appName} from './app.json';
// import { NavigationContainer } from '@react-navigation/native';
// import Providers from './src/navigation/appStack'


AppRegistry.registerComponent(appName, () => App);
// const App = () => {
//     return (
//       <NavigationContainer>
       
//       </NavigationContainer>
//     );
//   };
  
//   export default App;