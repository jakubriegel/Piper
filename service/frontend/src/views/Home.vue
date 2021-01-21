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
          Automate Home Automation
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
          How to start?
        </h2>

        <v-row justify="center">
          <a
            v-for="(link, i) in importantLinks"
            :key="i"
            :href="link.href"
            class="subheading mx-3"
            target="_blank"
          >
            {{ link.text }}
          </a>
        </v-row>
      </v-col>

      <v-col class="mb-5" cols="12">
        <h2 class="headline font-weight-bold mb-3">
          Possibilities
        </h2>

        <v-row justify="center">
          <a
            v-for="(eco, i) in ecosystem"
            :key="i"
            :href="eco.href"
            class="subheading mx-3"
            target="_blank"
          >
            {{ eco.text }}
          </a>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'Home',
  data: () => ({
    ecosystem: [
      {
        text: 'vuetify-loader',
        href: 'https://github.com/vuetifyjs/vuetify-loader'
      },
      {
        text: 'github',
        href: 'https://github.com/vuetifyjs/vuetify'
      },
      {
        text: 'awesome-vuetify',
        href: 'https://github.com/vuetifyjs/awesome-vuetify'
      }
    ],
    importantLinks: [
      {
        text: 'Documentation',
        href: 'https://vuetifyjs.com'
      },
      {
        text: 'Chat',
        href: 'https://community.vuetifyjs.com'
      },
      {
        text: 'Made with Vuetify',
        href: 'https://madewithvuejs.com/vuetify'
      },
      {
        text: 'Twitter',
        href: 'https://twitter.com/vuetifyjs'
      },
      {
        text: 'Articles',
        href: 'https://medium.com/vuetify'
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
