<template>
  <v-container>
    <v-row class="text-center">
      <v-col cols="12">
        <v-img
          :src="require('../assets/logo.png')"
          class="my-3"
          contain
          height="250"
        />
      </v-col>

      <v-col class="mb-4">
        <h1 class="display-2 font-weight-bold mb-3">
          Automate Your Routines
        </h1>

        <p class="subheading font-weight-regular">
          Modern smart home solutions provide a way to automate almost
          everything. Unfortunately it’s nearly impossible to handle all
          possible scenarios by manually created routines. Additionally all
          smart houses have different sets of devices. There should be a way for
          houses to automatically adjust to human behaviour. Piper is solution
          for that problem.
        </p>
      </v-col>

      <v-col class="mb-5" cols="12">
        <h2 class="headline font-weight-bold mb-3">
          Used technologies
        </h2>

        <v-row justify="center">
          <v-col cols="6" sm="4" md="3" v-for="(next, i) in techLogos" :key="i">
            <v-img :src="next.src" :alt="next.text" />
          </v-col>
        </v-row>
      </v-col>

      <v-col class="mb-5" cols="12">
        <h2 class="headline font-weight-bold mb-3">
          Features
        </h2>

        <v-row justify="center">
          <v-col
            cols="6"
            sm="6"
            md="4"
            v-for="(next, i) in features"
            :key="i"
            class="text-center pa-10"
          >
            <v-icon x-large :color="next.color">{{ next.icon }}</v-icon>
            <h3 class="mt-5">{{ next.name }}</h3>
            <p>{{ next.description }}</p>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'Home',
  data: () => ({
    features: [
      {
        name: 'Easy to integrate',
        icon: 'mdi-web',
        color: 'green',
        description:
          "We provide easy to integrate interface. Soo it's easy to invite Piper to your house and make your life simpler!"
      },
      {
        name: 'Scalable',
        icon: 'mdi-codepen',
        color: 'yellow',
        description:
          'Piper was designed to be easy scalable. All services are running on docker containers and you can switch between x86 and ARM64 CPU architectures.'
      },
      {
        name: 'AI based new routines suggestions',
        icon: 'mdi-lock-smart',
        color: 'black',
        description:
          'We suggest you routines right away to automate your daily basis. You no longer need to think about how to configure your smart devices. Piper will do it for you.'
      },
      {
        name: 'Responsive',
        icon: 'mdi-developer-board',
        color: 'dark grey',
        description:
          'The application is fully responsive and works on any type of screen and any device with a browser. So if your smart coffee maker has a browser and internet access, you can use it to set up routines.'
      },
      {
        name: 'AI based continuations suggestion',
        icon: 'mdi-brain',
        color: 'pink',
        description:
          'Piper also recommends extending your existing routines with new actions so your automation ideas can be even better!'
      },
      {
        name: 'Automatic synchronization with your house',
        icon: 'mdi-home-automation',
        color: 'accent',
        description:
          'Piper automatically syncs with your home. You no longer have to worry about manually reloading routines. You can control everything from your browser. You can turn routines on and off as well as edit them in real time.'
      }
    ],
    techLogos: [
      {
        text: 'Tensor Flow',
        src: '/tech-logos/tensorflow-logo.png'
      },
      {
        text: 'Spring',
        src: '/tech-logos/spring-logo.png'
      },
      {
        text: 'Vue',
        src: '/tech-logos/vue-logo-clean.png'
      },
      {
        text: 'Vuetify',
        src: '/tech-logos/vuetify-logo.png'
      },
      {
        text: 'MongoDB',
        src: '/tech-logos/MongoDB-logo.png'
      },
      {
        text: 'Kotlin',
        src: '/tech-logos/Kotlin-logo.png'
      },
      {
        text: 'Flask',
        src: '/tech-logos/flask-logo.png'
      },
      {
        text: 'Raspberry',
        src: '/tech-logos/raspberry-logo.png'
      }
    ]
  })
};
</script>
