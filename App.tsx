import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';
import EditScreen from './src/screens/homeScreen';
import HomeScreen from './src/screens/homeScreen';
import Providers from './src/navigation';
import { NavigationContainer } from '@react-navigation/native';


const Stack = createStackNavigator();

// const Stack = createStackNavigator<RootStackParamList>();

// export default function App() {
//   return (
//     <NavigationContainer>
//       <Stack.Navigator>
//         <Stack.Screen options={{headerShown: false}} name="Home" component={HomeScreen} />        
//         <Stack.Screen options={{headerShown: false}} name="Edit" component={EditScreen} />         
//       </Stack.Navigator>
//     </NavigationContainer>
//   );
// }

//Auth is Home Screen
//Main Edit

const AppStack = () => {

return (
  <NavigationContainer>
    <Providers/>
  </NavigationContainer>
);
};
export default AppStack;