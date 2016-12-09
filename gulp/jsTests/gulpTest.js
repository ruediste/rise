var utils = require('../gulpfile-utils.js');
var expect= require('chai').expect;
describe('Gulpfile Utils', function() {
  it('normalizeSrcs()', function() {
    expect(utils.private.normalizeSrcs('foo2')).to.deep.equal([{src: ['foo2']}]);
    expect(utils.private.normalizeSrcs(['foo1'])).to.deep.equal([{src: ['foo1']}]);
    expect(utils.private.normalizeSrcs(['foo','bar'])).to.deep.equal([{src: ['foo']},{src: ['bar']}]);
    expect(utils.private.normalizeSrcs([{src: ['*.css']}])).to.deep.equal([{src: ['*.css']}]);
  });
  it('normalizeSegments()', function() {
    expect(utils.private.normalizeSegments([['foo']])).to.deep.equal([{srcs:[{src: ['foo']}]}]);
  });
  it('normalizeBundle()', function() {
    expect(utils.private.normalizeBundle({segments:[['foo']]})).to.deep.equal({segments:[{srcs:[{src: ['foo']}]}]});
  });
});
