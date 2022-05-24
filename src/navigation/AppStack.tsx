import React, {useContext, useEffect} from 'react';
import HomeScreen from '../screens/homeScreen';
import EditScreen from '../screens/editScreen';
import EditorScreen from '../screens/editorScreen';
import ShuttleScreen from '../screens/shuttle';
import TrimScreen from '../screens/trim';
import AngleScreen from '../screens/angle';
import AdjustScreen from '../screens/adjustScreen';
import {createStackNavigator} from '@react-navigation/stack';

const Stack = createStackNavigator();

const AppStack = () => {

return (
        <Stack.Navigator
        initialRouteName={'Home'}
        screenOptions={{
        cardStyle: { backgroundColor: '#FFFFFF' },
        headerShown: false,
        headerStyle: {
            backgroundColor: 'transparent',
        },
        headerTintColor: '#fff',
        }}
        >

        <Stack.Screen name="Home" component={HomeScreen} options={{header: ()=> null}} />
        <Stack.Screen name="Edit" component={EditScreen} options={{header: ()=> null}} />
        <Stack.Screen name="Editor" component={EditorScreen} options={{header: ()=> null}} />
        <Stack.Screen name="Shuttle" component={ShuttleScreen} options={{header: ()=> null}} />
        <Stack.Screen name="Trim" component={TrimScreen} options={{header: ()=> null}} />
        <Stack.Screen name="Angle" component={AngleScreen} options={{header: ()=> null}} />
        <Stack.Screen name="Adjust" component={AdjustScreen} options={{header: () => null}} />
        </Stack.Navigator>
  );
};
export default AppStack;