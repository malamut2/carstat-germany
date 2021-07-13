<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
  </div>
</template>

<script>
export default {

  name: 'HelloWorld',

  data() {
    return {
      msg: ''
    }
  },

  mounted() {
    fetch("/api/cars/hello")
        .then((response) => response.text())
        .then((data) => {
          this.msg = data;
        });
  }

}
</script>
