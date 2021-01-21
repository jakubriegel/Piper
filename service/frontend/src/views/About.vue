<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <div class="about">
          <h1 class="text-center my-3">About Piper</h1>
          <p class="text-justify">
            Piper is a machine learning based system, that is able enhance smart
            homes with auto-mated creation of routines based on real life
            events. This work presents key aspects of the system, describes its
            development process and provides guides for prospective integrators
            and users. First chapter discusses motivation for the project and
            defines its purpose. The next one depicts architectural concepts
            used in the system. The third chapter documents characteristics and
            implementation of Piper’s modules. It is followed by the section
            about deploying the system. Next two chapters are guides for
            integrators and end users. They are followed by report from tests of
            the system. The paper is ended with conclusions designing,
            developing and testing Piper.
          </p>
          <p>
            Piper is project for engineering thesis written on Poznan University
            of Technology, by
            <i>Wojciech Kasperski, Jakub Riegiel, Rafał Ewiak </i>and<i>
              Michał Kalinowski </i
            >.
          </p>
          <p>Project was supervised by <i>dr inż. Marek Kraft.</i></p>
        </div>
      </v-col>
      <v-col class="mb-5" cols="12">
        <h2 class="headline font-weight-bold mb-3 text-center">
          Authors
        </h2>

        <v-row justify="center">
          <v-col
            cols="12"
            sm="6"
            md="3"
            v-for="(next, i) in authors"
            :key="i"
            class="text-center pa-10"
          >
            <v-img :src="next.src" :alt="next.name" class="rounded-circle" />
            <h3 class="mt-5">{{ next.name }}</h3>
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
    authors: [
      {
        name: 'Wojciech Kasperski',
        src: '/authors-img/wk-avatar.png'
      },
      {
        name: 'Jakub Riegel',
        src: '/authors-img/jr-avatar.jpeg'
      },
      {
        name: 'Michał Kalinowski',
        src: '/authors-img/mk-avatar.jpg'
      },
      {
        name: 'Rafał Ewiak',
        src: '/authors-img/re-avatar.jpg'
      }
    ]
  })
};
</script>
